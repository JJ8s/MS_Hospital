Para hacer un resumen estos son todos los microservicios alterados 
para funcionar completamente en docker ayudandonos a iniciar todos 
los microservicios al mismo tiempo y evitar errores de localhost


MicroServicios funcionando con Docker, comandos basicos:

./mvnw clean package -DskipTests     para limpiar todos los microservicios (el -DskipTests nos ayuda a limpiar todos los microservicios y compilarlos 
                                                                            sin la necesidad de ejecutar los tests de estos mismos en caso de errores 
                                                                            evitarlos, ya que estos no afectan el funcionamiento importante de los 
                                                                                                        microservicios)

docker-compose up -d --build         para construir e iniciar todos los microservicios

docker-compose down                  para apagar todos los microservicios

docker-compose logs -f               para ver la actidad de los microservicios

Mi parte es encargarme de los micro-servicios Eureka-hospital, Api-gateway, Ms-user y Ms-auth

Eureka Server (Registry): Base de datos dinámica en memoria donde cada microservicio inyecta de forma autónoma su IP, puerto y estado de salud al arrancar, resolviendo de forma centralizada la localización física de los nodos de la red sin usar configuraciones estáticas (hardcoding).

API Gateway (Proxy/Routing): Punto único perimetral (puerto 8080) construido sobre programación reactiva no bloqueante. Evalúa las peticiones mediante Predicados de ruta (ej. /api/facturas/), consulta a Eureka para resolver la IP del destino y enruta el tráfico aplicando Filtros transversales de seguridad y CORS.

ms-user (Identity Data Layer): Microservicio puramente transaccional y aislado de dominio (IAM). Su única función es la persistencia y lectura en base de datos de identidades, perfiles y contraseñas encriptadas con la función hash criptográfica adaptativa BCrypt bajo un modelo RBAC (Role-Based Access Control).

ms-auth (Authorization Server): Motor de autenticación Stateless (sin estado). Intercepta credenciales, las valida delegando en ms-user y emite un token JWT firmado criptográficamente por simetría (HS512 + secreto). Permite a los demás servicios descifrar e identificar localmente al usuario y sus roles sin consultar la base de datos en cada petición.

tambien aclarar que este proyecto requiere de la carpeta .mvn/wrapper/maven-wrapper.propperties:
wrapperVersion=3.3.4
distributionType=only-script
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.16/apache-maven-3.9.16-bin.zip
se incluye aqui por la razón de que no sé implementar archivos ocultos a github ya que este no lo permite.
