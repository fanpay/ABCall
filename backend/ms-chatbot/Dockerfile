# Use an official Python runtime as the base image
FROM python:3.10-slim

LABEL author="fpayan@uniandes.edu.co"

# Set the working directory in the container to /app
WORKDIR /app

# Copy the rest of the application code to the container
COPY . /app
#COPY ./.env .
COPY ./requirements.txt .

# Install the dependencies
RUN pip install -r requirements.txt


# Set environment variables
ENV FLASK_APP="src/app.py"
ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONFAULTHANDLER 1

# Expose port 9876 for the Flask development server to listen on
EXPOSE 9878

# Define the command to run the Flask development server
CMD ["flask", "run", "--host=0.0.0.0", "-p 9878"]
#CMD ["gunicorn", "--bind", "0.0.0.0:9878", "app:app"]
#CMD ["gunicorn", "--bind", "0.0.0.0:9878", "src.app:app"]