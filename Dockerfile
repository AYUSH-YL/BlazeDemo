# Step 1: Use an official Maven image backed by Java 17 as our runtime foundation
FROM maven:3.8.8-eclipse-temurin-17

# Step 2: Install Google Chrome inside the Linux container environment
# Step 2: Install Google Chrome inside the Linux container environment
RUN apt-get update && apt-get install -y wget gnupg2 \
    && wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Step 3: Set up the internal working directory paths inside the container
WORKDIR /app

# Step 4: Copy pom.xml first to take advantage of Docker caching for dependencies
COPY pom.xml .

# Pre-download all Maven dependencies so builds run faster on subsequent cycles
RUN mvn dependency:go-offline -B

# Step 5: Copy the remaining source code and configurations into the workspace
COPY src ./src

# Step 6: Inject our custom environment variable to let BaseTest know it's in Docker
ENV RUNNING_IN_DOCKER=true

# Step 7: The command that runs automatically when the container boots up
CMD ["mvn", "clean", "test"]