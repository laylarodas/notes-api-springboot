package dev.layla.notesapi.user;

import dev.layla.notesapi.note.Note;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Entidad User que también implementa UserDetails de Spring Security.
 * 
 * UserDetails es la interfaz que Spring Security usa para representar
 * un usuario autenticado. Al implementarla, nuestra entidad User puede
 * ser usada directamente por el framework de seguridad.
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /**
     * Password hasheado con BCrypt.
     * NUNCA se guarda en texto plano.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Rol del usuario para autorización.
     * Por ahora solo USER, pero podrías agregar ADMIN, etc.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notes = new ArrayList<>();

    // Constructor vacío requerido por JPA
    protected User() {}

    // Constructor para registro de usuarios
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = Role.USER;
        this.createdAt = LocalDateTime.now();
    }

    // ==================== GETTERS DE LA ENTIDAD ====================

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<Note> getNotes() { return notes; }

    // ==================== SETTERS ====================

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(Role role) { this.role = role; }

    // ==================== IMPLEMENTACIÓN DE UserDetails ====================
    
    /**
     * El "username" para Spring Security será el email.
     * Es más común usar email que un username separado.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Retorna el password hasheado.
     * Spring Security lo comparará con el password que envíe el usuario.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Retorna los roles/permisos del usuario.
     * Spring Security usa esto para autorización (@PreAuthorize, etc.)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convertimos el rol a un GrantedAuthority
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * Indica si la cuenta no ha expirado.
     * Podrías agregar lógica de expiración si lo necesitas.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // La cuenta nunca expira (por ahora)
    }

    /**
     * Indica si la cuenta no está bloqueada.
     * Útil para implementar bloqueo después de X intentos fallidos.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // La cuenta nunca se bloquea (por ahora)
    }

    /**
     * Indica si las credenciales no han expirado.
     * Útil para forzar cambio de contraseña periódicamente.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Las credenciales nunca expiran (por ahora)
    }

    /**
     * Indica si el usuario está habilitado.
     * Útil para implementar verificación de email.
     */
    @Override
    public boolean isEnabled() {
        return true; // El usuario siempre está habilitado (por ahora)
    }
}
