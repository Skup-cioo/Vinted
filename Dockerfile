# Używamy obrazu Javy jako bazy
FROM  openjdk:17-jdk-alpine

# Instalujemy brakujące zależności dla AWT i POI
RUN apk add --no-cache \
    freetype \
    fontconfig \
    ttf-dejavu
# Ustawiamy katalog roboczy

WORKDIR /app

# Kopiujemy plik JAR aplikacji do kontenera
COPY /target/vinted-1.0.3-SNAPSHOT.jar app.jar

# Skopiuj skrypt do kontenera
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# Eksponujemy port, na którym działa aplikacja
EXPOSE 8080

# Uruchamiamy aplikację
ENTRYPOINT ["java", "-jar", "app.jar"]