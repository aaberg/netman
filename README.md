
## NATS Messaging

The application uses NATS for pub/sub messaging. The NATS server is included in the Docker Compose setup.

### Task Trigger Processing

The application subscribes to the `task.trigger.due` subject to process due task triggers. When a message is published to this subject, the application will:
1. Find all pending triggers whose trigger time has passed
2. Mark the corresponding tasks as "Due"
3. Mark the triggers as "Triggered"

### Using NATS CLI

To manually trigger task processing, you can publish a message using the NATS CLI:

#### Install NATS CLI

```bash
# macOS
brew install nats-io/nats-tools/nats

# Windows (using Scoop)
scoop install nats

# Linux
curl -sf https://binaries.nats.dev/nats-io/natscli/nats@latest | sh

# Or download from: https://github.com/nats-io/natscli/releases
```

#### Publish a Message

```bash
# Publish to the task.trigger.due subject (no payload needed)
nats pub task.trigger.due ""

# Or with confirmation
nats pub task.trigger.due "" --count 1
```

The subscriber uses a queue group (`task-trigger-processors`), which ensures that only one instance of the application will process each message when multiple instances are running. This enables horizontal scaling of the application.


## Contributing

- Use conventional commits if possible (feat, fix, docs, chore, etc.).
- Create feature branches from main.
- Add/adjust tests where appropriate.
- Run linters/formatters before committing.

## License

Add your project's license information here (e.g., MIT, Apache-2.0).
