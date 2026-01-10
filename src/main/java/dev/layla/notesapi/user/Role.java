package dev.layla.notesapi.user;

/**
 * Roles disponibles para los usuarios.
 * 
 * Spring Security usa estos roles para autorización.
 * Por convención, los roles se prefijan con "ROLE_" automáticamente.
 */
public enum Role {
    USER,   // Usuario normal - puede gestionar sus propias notas
    ADMIN   // Administrador - podría tener acceso a todo (futuro)
}
