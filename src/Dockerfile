# Use official OpenJDK 17 JDK slim image as base
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy source file to the working directory
COPY src/ParkingSimulator.java .

# Compile the source file
RUN javac ParkingSimulator.java

# Run the inner static class Program (which contains the main method)
CMD ["java", "ParkingSimulator$Program"]

