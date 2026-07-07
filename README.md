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

