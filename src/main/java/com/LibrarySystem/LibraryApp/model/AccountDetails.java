package com.LibrarySystem.LibraryApp.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@Entity
@Table(name = "AccountTable")
public class AccountDetails implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id", table = "UserTable", nullable = true)
    private User user;
    
    @Column(name = "userId", nullable = true)
    private Long userId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "registrationDate", nullable = false)
    private Date registrationDate;
    
    @Column(name = "isAccountLocked", nullable = false)
    private boolean isAccountLocked;

    @Column(name = "isAccountExpired", nullable = false)
    private boolean isAccountExpired;

    @Column(name = "isCredentialExpired", nullable = false)
    private boolean isCredentialExpired;
    
    @Column(name = "isAccountEnabled", nullable = false)
    private boolean isAccountEnabled;
    
    @Column(name = "authorizedRoles", nullable = false)
    private String roles;
    
    @Transient
    private List<GrantedAuthority> authorities = new ArrayList<>();

    @OneToMany(mappedBy = "accountId", fetch = FetchType.LAZY)
    private Set<BorrowRegistration> borrowRegistrations;


    private static final String DEFAULT_ROLE = "USER";
    private static final String DEFAULT_USERNAME = "DEFAULT_NAME";
    private static final String DEFAULT_PASSWORD = "DEFAULT_PASSWORD";
    private static final String DEFAULT_REGISTRATION_DATE = "2000-01-01";

    public AccountDetails() {
        username = DEFAULT_USERNAME;
        password = DEFAULT_PASSWORD;
        roles = DEFAULT_ROLE;
        authorities = parseAuthorities(DEFAULT_ROLE);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            registrationDate = dateFormat.parse(DEFAULT_REGISTRATION_DATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public AccountDetails(long id, User user, String username, String password, Date registrationDate,
            boolean isAccountLocked, boolean isAccountExpired, boolean isCredentialExpired, boolean isAccountEnabled,
            String roles) {
        this.id = id;
        this.user = user;
        this.username = username;
        this.password = password;
        this.registrationDate = registrationDate;
        this.isAccountLocked = isAccountLocked;
        this.isAccountExpired = isAccountExpired;
        this.isCredentialExpired = isCredentialExpired;
        this.isAccountEnabled = isAccountEnabled;
        this.roles = roles;
        authorities = parseAuthorities(roles);
    }

    public AccountDetails(String username, String password, String roles) {
        this.username = username;
        this.password = password;
        this.authorities = parseAuthorities(roles);
        this.roles = roles;
    
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            registrationDate = dateFormat.parse(DEFAULT_REGISTRATION_DATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private List<GrantedAuthority> parseAuthorities(String authorities){
        List<GrantedAuthority> authoritieList = new LinkedList<>();
        
        for (String str : authorities.split(",")) {
            String prefix = "ROLE_";
            str = prefix + str;
            SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(str);
            authoritieList.add(grantedAuthority);
        }

        return authoritieList;
    }

    public void addRole(String role){
        if(roles.isEmpty())
           role += ",";
        else 
            role = "," + role;
            
        roles += role;
    }

    public void deleteRole(String role){
        int indexStart = roles.indexOf(role);
        int indexEnd = role.length();

        if(indexStart == 0)
            indexEnd += 1;
        else
            indexStart += 1;


            roles = roles.substring(indexStart, indexEnd);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        authorities = parseAuthorities(roles);
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isAccountLocked() {
        return isAccountLocked;
    }

    public void setAccountLocked(boolean isAccountLocked) {
        this.isAccountLocked = isAccountLocked;
    }

    public boolean isAccountExpired() {
        return isAccountExpired;
    }

    public void setAccountExpired(boolean isAccountExpired) {
        this.isAccountExpired = isAccountExpired;
    }

    public boolean isCredentialExpired() {
        return isCredentialExpired;
    }

    public void setCredentialExpired(boolean isCredentialExpired) {
        this.isCredentialExpired = isCredentialExpired;
    }

    public boolean isAccountEnabled() {
        return isAccountEnabled;
    }

    public void setAccountEnabled(boolean isAccountEnabled) {
        this.isAccountEnabled = isAccountEnabled;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
        authorities = parseAuthorities(roles);
    }

    public Long getId(){
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    
    public Long getUserId() {
        return userId;
    }


    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }


    public static String getDefaultRole() {
        return DEFAULT_ROLE;
    }


    public static String getDefaultUsername() {
        return DEFAULT_USERNAME;
    }


    public static String getDefaultPassword() {
        return DEFAULT_PASSWORD;
    }


    public static String getDefaultRegistrationDate() {
        return DEFAULT_REGISTRATION_DATE;
    }

    //public void setAuthorities(List<GrantedAuthority> authorities) {
    //    this.authorities = authorities;
    //}
    
    
}
