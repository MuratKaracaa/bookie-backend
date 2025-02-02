package com.karacam.bookie.services;

import com.karacam.bookie.configs.RedisConfig;
import com.karacam.bookie.core.BaseConstants;
import com.karacam.bookie.core.config.ConfigManager;
import com.karacam.bookie.core.enums.Role;
import com.karacam.bookie.core.localization.LocalizationManager;
import com.karacam.bookie.core.session.SessionManager;
import com.karacam.bookie.core.utils.EmailUtility;
import com.karacam.bookie.dtos.request.*;
import com.karacam.bookie.dtos.response.AuthResponse;
import com.karacam.bookie.entities.AppUser;
import com.karacam.bookie.repositories.AppUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {
    private final AppUserRepository appUserRepository;
    private final RedisTemplate<String, Object> primaryRedisTemplate;
    private final LocalizationManager localizationManager;
    private final EmailUtility emailUtility;
    private final PasswordEncoder passwordEncoder;
    private final ConfigManager configManager;
    private final SessionManager sessionManager;

    private final String EMAIL_FIELD = "email";
    private final String PASSWORD_FIELD = "password";
    private final String FIRST_NAME_FIELD = "firstName";
    private final String LAST_NAME_FIELD = "lastName";
    private final String OTP_NUMBER_FIELD = "otpNumber";
    private final String NEW_PASSWORD_FIELD = "newPassword";
    private final String OLD_PASSWORD_FIELD = "oldPassword";

    @Autowired
    public AuthService(AppUserRepository appUserRepository, @Qualifier(RedisConfig.PRIMARY_REDIS_TEMPLATE) RedisTemplate<String, Object> primaryRedisTemplate, LocalizationManager localizationManager, EmailUtility emailUtility, PasswordEncoder passwordEncoder, ConfigManager configManager, SessionManager sessionManager) {
        this.appUserRepository = appUserRepository;
        this.primaryRedisTemplate = primaryRedisTemplate;
        this.localizationManager = localizationManager;
        this.emailUtility = emailUtility;
        this.passwordEncoder = passwordEncoder;
        this.configManager = configManager;
        this.sessionManager = sessionManager;
    }

    public AuthResponse registerUser(RegisterUserRequest requestBody) {
        Optional<AppUser> optionalAppUser = this.appUserRepository.findByEmail(requestBody.getEmail());
        if (optionalAppUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(localizationManager.getLocalization("user_already_exists_error"), requestBody.getEmail()));
        }
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String sessionId = request.getAttribute(BaseConstants.ANONYMOUS_SESSION_ID_PAYLOAD).toString();

        String transactionKey = generateTransactionKey(configManager.getConfig("registration_token_prefix"));
        String otpNumber = generateOtpNumber();
        HashOperations<String, String, String> hashOperations = this.primaryRedisTemplate.opsForHash();
        Map<String, String> fields = new HashMap<>();
        fields.put(EMAIL_FIELD, requestBody.getEmail());
        fields.put(PASSWORD_FIELD, requestBody.getPassword());
        fields.put(FIRST_NAME_FIELD, requestBody.getFirstName());
        fields.put(LAST_NAME_FIELD, requestBody.getLastName());
        fields.put(OTP_NUMBER_FIELD, otpNumber);

        hashOperations.putAll(transactionKey, fields);
        this.primaryRedisTemplate.expire(transactionKey, this.configManager.getIntConfig("cache_expiry_duration", 300000), TimeUnit.MILLISECONDS);
        this.sessionManager.addCustomDataToSession(BaseConstants.CURRENT_TRANSACTION_KEY, transactionKey);

        String emailSubject = localizationManager.getLocalization("email_verification_subject");
        String localizedEmailText = localizationManager.getLocalization("email_verification_message");

        String emailText = String.format(localizedEmailText, requestBody.getFirstName() + " " + requestBody.getLastName(), otpNumber);

        this.emailUtility.sendHtmlEmail(requestBody.getEmail(), this.configManager.getConfig("email_sender_account"), emailSubject, emailText);


        return AuthResponse.builder()
                .sessionId(sessionId)
                .build();
    }

    @Transactional
    public ResponseEntity<Void> verifyEmail(OTPRequest requestBody) {
        HashOperations<String, String, String> hashOperations = this.primaryRedisTemplate.opsForHash();
        String transactionKey = this.sessionManager.getCustomDataFromSession(BaseConstants.CURRENT_TRANSACTION_KEY).toString();
        Map<String, String> userDetails = hashOperations.entries(transactionKey);

        if (userDetails.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, localizationManager.getLocalization("verification_token_not_found_error"));
        }

        if (!Objects.equals(requestBody.getOtpNumber(), userDetails.get(OTP_NUMBER_FIELD))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, localizationManager.getLocalization("otp_number_not_matching_error"));
        }

        AppUser appUser = AppUser.builder()
                .email(userDetails.get(EMAIL_FIELD))
                .authority(Role.USER)
                .firstName(userDetails.get(FIRST_NAME_FIELD))
                .lastName(userDetails.get(LAST_NAME_FIELD))
                .password(this.passwordEncoder.encode(userDetails.get(PASSWORD_FIELD)))
                .build();

        this.appUserRepository.save(appUser);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public AuthResponse resetPasswordInit(ResetPasswordInitRequest requestBody) {
        boolean userExists = this.appUserRepository.existsByEmail(requestBody.getEmail());

        if (!userExists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(this.localizationManager.getLocalization("user_not_found_error"), requestBody.getEmail()));
        }

        HttpSession session = this.sessionManager.getCurrentSession();
        String transactionKey = generateTransactionKey(this.configManager.getConfig("password_reset_prefix"));

        HashOperations<String, String, String> hashOperations = this.primaryRedisTemplate.opsForHash();

        Map<String, String> fields = new HashMap<>();
        fields.put(EMAIL_FIELD, requestBody.getEmail());

        hashOperations.putAll(transactionKey, fields);
        this.sessionManager.addCustomDataToSession(BaseConstants.CURRENT_TRANSACTION_KEY, transactionKey);

        return AuthResponse.builder().sessionId(session.getId()).build();
    }

    public ResponseEntity<Void> resetPasswordSubmitNewPassword(ResetPasswordSubmitNewPasswordRequest requestBody) {
        String transactionKey = this.sessionManager.getCustomDataFromSession(BaseConstants.CURRENT_TRANSACTION_KEY).toString();
        HashOperations<String, String, String> hashOperations = this.primaryRedisTemplate.opsForHash();
        String email = hashOperations.get(transactionKey, EMAIL_FIELD);

        AppUser user = this.appUserRepository.findByEmail(email).get();

        if (!this.passwordEncoder.matches(requestBody.getOldPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, localizationManager.getLocalization("old_password_not_matching_error"));
        }

        Map<String, String> newValues = new HashMap<>();
        newValues.put(NEW_PASSWORD_FIELD, requestBody.getNewPassword());
        newValues.put(OLD_PASSWORD_FIELD, requestBody.getOldPassword());
        newValues.put(OTP_NUMBER_FIELD, generateOtpNumber());

        hashOperations.putAll(transactionKey, newValues);
        this.primaryRedisTemplate.expire(transactionKey, this.configManager.getIntConfig("cache_expiry_duration", 300000), TimeUnit.MILLISECONDS);


        String emailSubject = localizationManager.getLocalization("password_reset_subject");
        String localizedEmailText = localizationManager.getLocalization("password_reset_message");

        String emailText = String.format(localizedEmailText, user.getFirstName() + " " + user.getLastName(), newValues.get(OTP_NUMBER_FIELD));

        this.emailUtility.sendHtmlEmail(email, this.configManager.getConfig("email_sender_account"), emailSubject, emailText);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Transactional
    public ResponseEntity<Void> resetPasswordConfirmNewPassword(OTPRequest requestBody) {
        String transactionKey = this.sessionManager.getCustomDataFromSession(BaseConstants.CURRENT_TRANSACTION_KEY).toString();
        HashOperations<String, String, String> hashOperations = this.primaryRedisTemplate.opsForHash();
        Map<String, String> details = hashOperations.entries(transactionKey);

        if (!Objects.equals(requestBody.getOtpNumber(), details.get(OTP_NUMBER_FIELD))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, localizationManager.getLocalization("otp_number_not_matching_error"));
        }

        AppUser user = this.appUserRepository.findByEmail(details.get(EMAIL_FIELD)).get();

        user.setPassword(this.passwordEncoder.encode(details.get(NEW_PASSWORD_FIELD)));
        this.appUserRepository.save(user);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    public ResponseEntity<Void> changePasswordInit(ChangePasswordRequest requestBody) {
        String email = this.sessionManager.getCurrentUsername();

        Optional<AppUser> optionalAppUser = this.appUserRepository.findByEmail(email);

        if (optionalAppUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(localizationManager.getLocalization("user_not_found_error"), email));
        }

        AppUser appUser = optionalAppUser.get();

        if (!this.passwordEncoder.matches(requestBody.getOldPassword(), appUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, localizationManager.getLocalization("old_password_not_matching_error"));
        }

        String transactionKey = generateTransactionKey(this.configManager.getConfig("password_reset_prefix"));
        String otpNumber = generateOtpNumber();
        HashOperations<String, String, String> hashOperations = this.primaryRedisTemplate.opsForHash();
        Map<String, String> fields = new HashMap<>();
        fields.put(EMAIL_FIELD, email);
        fields.put(NEW_PASSWORD_FIELD, requestBody.getNewPassword());
        fields.put(OTP_NUMBER_FIELD, otpNumber);
        hashOperations.putAll(transactionKey, fields);
        this.primaryRedisTemplate.expire(transactionKey, this.configManager.getIntConfig("cache_expiry_duration", 300000), TimeUnit.MILLISECONDS);
        this.sessionManager.addCustomDataToSession(BaseConstants.CURRENT_TRANSACTION_KEY, transactionKey);

        String emailSubject = localizationManager.getLocalization("change_password_subject");
        String localizedEmailText = localizationManager.getLocalization("change_password_message");

        String emailText = String.format(localizedEmailText, appUser.getFirstName() + " " + appUser.getLastName(), otpNumber);

        this.emailUtility.sendHtmlEmail(email, this.configManager.getConfig("email_sender_account"), emailSubject, emailText);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }


    @Transactional
    public ResponseEntity<Void> changePasswordConfirm(OTPRequest requestBody) {
        String transactionKey = this.sessionManager.getCustomDataFromSession(BaseConstants.CURRENT_TRANSACTION_KEY).toString();
        HashOperations<String, String, String> hashOperations = this.primaryRedisTemplate.opsForHash();
        Map<String, String> userDetails = hashOperations.entries(transactionKey);

        if (userDetails.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, localizationManager.getLocalization("verification_token_not_found_error"));
        }

        if (!Objects.equals(requestBody.getOtpNumber(), userDetails.get(OTP_NUMBER_FIELD))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, localizationManager.getLocalization("otp_number_not_matching_error"));
        }

        Optional<AppUser> optionalAppUser = this.appUserRepository.findByEmail(userDetails.get(EMAIL_FIELD));

        if (optionalAppUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(localizationManager.getLocalization("user_not_found_error"), userDetails.get(EMAIL_FIELD)));
        }

        AppUser appUser = optionalAppUser.get();

        String newHashedPassword = this.passwordEncoder.encode(userDetails.get(NEW_PASSWORD_FIELD));

        appUser.setPassword(newHashedPassword);
        this.appUserRepository.save(appUser);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public AuthResponse login(LoginRequest requestBody) {
        HttpSession session = this.sessionManager.createAuthenticatedSession(requestBody.getEmail(), requestBody.getPassword());

        return AuthResponse.builder().sessionId(session.getId()).build();
    }

    private String generateTransactionKey(String prefix) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        int tokenLength = this.configManager.getIntConfig("verification_token_length", 128);
        while (sb.length() < tokenLength) {
            sb.append(String.format("%08x", r.nextInt()));
        }
        return prefix + sb.substring(0, tokenLength);
    }

    private String generateOtpNumber() {
        Random r = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        int otpLength = this.configManager.getIntConfig("otp_length", 6);
        while (stringBuilder.length() < otpLength) {
            stringBuilder.append(r.nextInt(10));
        }

        return stringBuilder.toString();
    }
}
