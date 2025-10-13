# FlareSolverr RabbitMQ Bridge (service)

> A simple microservice that allows to post requests to
> [FlareSolverr](https://github.com/FlareSolverr/FlareSolverr) via [RabbitMQ](https://www.rabbitmq.com/).

## Configuration

Configuration is done using environment variables:

* `PORT`: Port for the HTTP endpoint (default `8080`, only change when running locally!)
* `RMQ_HOST`: Host for RabbitMQ (default `localhost`)
* `RMQ_PORT`: Port for RabbitMQ (default `5672`)
* `RMQ_USER`: Username for RabbitMQ (default `guest`)
* `RMQ_PASSWORD`: Password for RabbitMQ (default `guest`)
* `RMQ_VHOST`: Virtual host for RabbitMQ (default `/`)
* `RMQ_QUEUE_FLARESOLVERR_REQUESTS`: Name of the queue for FlareSolverr requests (default `flaresolverr-requests`)
* `FLARESOLVERR_URL`: URL for the FlareSolverr service (default `http://localhost:8191`)

## API

The service accepts JSON requests with the following structure:

```json
{
  "request": "Expects a request object according to the FlareSolverr API"
}
```

The request object is passed to the FlareSolverr service and the response is returned.
Please refer to
the [FlareSolverr API documentation](https://github.com/FlareSolverr/FlareSolverr?tab=readme-ov-file#-requestget) for
details.

When the `postData` property is set, a POST request is sent to the FlareSolverr service, otherwise a GET request is
performed.

Note: Encapsulating the request in an object is necessary to allow for additional properties in the future.

The response is returned as is, without any processing.

## Run with Docker

With the configuration stored in a file `.env`, the service can be run as follows:

```bash
docker run --rm \
           -p 8080:8080 \
           --env-file .env \
           mrtux/flaresolverr-rmq-bridge-service:latest
```

Please make sure to pin the container to a specific version in a production environment.

The GitHub project stores [artifact attestations for the Docker image](https://github.com/netz39/ansible-role-host-docker/attestations).

## Development

Version numbers are determined with [jgitver](https://jgitver.github.io/).
If you encounter a project version `0` there is an issue with the jgitver generator.

## Maintainers

* Stefan Haun ([@penguineer](https://github.com/penguineer))

## Contributing

PRs are welcome!

If possible, please stick to the following guidelines:

* Keep PRs reasonably small and their scope limited to a feature or module within the code.
* If a large change is planned, it is best to open a feature request issue first, then link subsequent PRs to this
  issue, so that the PRs move the code towards the intended feature.

If you miss a LaTeX package, which cannot be included in the request archive, or miss a language, please do not hesitate
to open an issue or PR.

## License

[MIT](LICENSE.txt) © 2025 Stefan Haun and contributors
