# Vinted
Program stworzony w celu wyciągania, informacji / statystyk z aplikacji Vinted. 
 
Utworzony obraz docker, oraz docker-compose w celu uruchamiania aplikacji na różnych środowiskach.
 
W projekcie jest również zawarta kolekcja postmanowa do komunikacji z serwerem. 
 
**Aby uruchomić projekt potrzebujemy:**
- pobrać projekt z GIT'a 
- zbudowac paczke poleceniem mvn clean install -DskipTests
- użyć polecenia docker-compose up, aby zbudować obraz i uruchomić kontener 
-  W tym momencie kontenery z naszą aplikacją powinny wstać co oznacza, że appka działa
   
**Swagger dostepny** http://localhost:8080/swagger-ui/index.html#/
