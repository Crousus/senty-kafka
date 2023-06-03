# Use the official Node.js 14 image as the base image
FROM node:14

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json to the working directory
COPY package*.json ./

# Install project dependencies
RUN npm install

# Copy the entire project directory to the working directory
COPY . .

# Build the Next.js application

# Expose the port that the application will run on
EXPOSE 3000

# Set the command to start the Next.js application
CMD ["npm", "run", "dev"]
