# Use an official Python runtime as a parent image
FROM python:3.10-slim-buster

# Set the working directory in the container to /app
WORKDIR /app

# Install any needed packages specified in requirements.txt
RUN pip install --no-cache-dir torch torchvision torchaudio -f https://download.pytorch.org/whl/cpu/torch_stable.html
RUN pip install --no-cache-dir fastapi uvicorn transformers

# Download the model from Hugging Face Model Hub
RUN transformers-cli download jb2k/bert-base-multilingual-cased-language-detection

# Add the current directory contents into the container at /app
ADD . /app

# Make port 2000 available to the world outside this container
EXPOSE 2000

# Run app.py when the container launches
CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "2000"]
