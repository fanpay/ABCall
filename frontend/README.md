# ABCall - Frontend

## Endpoint

- [URL Producción](https://abcall-uniandes-74418e05b668.herokuapp.com/)


## Despliegue en Heroku

Instalar primero el CLI de Heroku (macOS):
```bash
brew tap heroku/brew && brew install heroku
```

Iniciar sesión en Heroku:
```bash
heroku container:login
```

Crear la aplicación en Heroku. Utiliza la aplicación web: www.heroku.com y ponle el nombre que desees:
```bash
abcall-uniandes
```

También puedes usar el CLI.
```bash
heroku create abcall-uniandes
```

Construir la imagen de Docker:
```bash
docker build -t abcall-web-prod .
```

Subir la imagen a Heroku:
```bash
heroku container:push web --app abcall-uniandes
```

Desplegar la imagen en Heroku:
```bash
heroku container:release web --app abcall-uniandes
```

Revisar la aplicación en Heroku:
```bash
heroku open --app abcall-uniandes
```

Revisar los logs de la aplicación en Heroku:
```bash
heroku logs --tail --app abcall-uniandes
```

Modificar el número de instancias de la aplicación en Heroku:
```bash
heroku ps:scale web=1 --app abcall-uniandes
```

Eliminar la aplicación en Heroku:
```bash
heroku apps:destroy --app abcall-uniandes
```

Información:
> https://medium.com/travis-on-docker/how-to-run-dockerized-apps-on-heroku-and-its-pretty-great-76e07e610e22