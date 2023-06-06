# Use the official Node.js 14 image as the base image
FROM node:19-alpine3.17

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json to the working directory
COPY package*.json ./

# Install project dependencies
RUN npm install

# Copy the entire project directory to the working directory
COPY . .

#Replace .env with .env.docker to apply docker specific environment variables
RUN mv .env.docker .env

RUN npm run postinstall

# Expose the port that the application will run on
EXPOSE 3000

# Set the command to start the Next.js application
CMD ["npm", "run", "dev"]
