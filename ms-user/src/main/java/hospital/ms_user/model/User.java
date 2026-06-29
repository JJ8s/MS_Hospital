package hospital.ms_user.model;

import java.util.Set;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor  // <-- Genera el constructor vacío que Swagger necesita para mapear el esquema
@AllArgsConstructor // <-- Buena práctica por si necesitas instanciar con datos completos
public class User {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;


// Campo único para el correo generado: nombre.apellido@hospital.com
@Column(name = "autoemail", unique = true, nullable = false)
private String autoEmail;

@NotBlank(message = "El nombre es obligatorio")
private String nombre;

@NotBlank(message = "El apellido es obligatorio")
private String apellido;

@NotBlank(message = "El número de teléfono es obligatorio")
@Size(min = 9, message = "Mínimo 9 dígitos")
@Column(unique = true, nullable = false)
private String telefono;

@NotBlank(message = "La contraseña es obligatoria")
@Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
@Column(nullable = false)
private String contrasena; // Cambiado para coincidir con tu último DTO

private boolean active = true;

@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(
    name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
)
private Set<Rol> roles;

}