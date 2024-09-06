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
RUN apt-get update && apt-get -y install libpq-dev gcc 
RUN pip install --upgrade pip
RUN pip install -r requirements.txt


# Set environment variables
ENV FLASK_APP="main.py"
ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONFAULTHANDLER 1
#ENV FLASK_DEBUG=1
#ENV FLASK_ENV=development

# Expose port 9876 for the Flask development server to listen on
EXPOSE 9876

# Define the command to run the Flask development server
CMD ["flask", "run", "--host=0.0.0.0", "-p 9876"]
#CMD ["gunicorn", "--bind", "0.0.0.0:9876", "app:app"]
#CMD ["gunicorn", "--bind", "0.0.0.0:9876", "main:app"]