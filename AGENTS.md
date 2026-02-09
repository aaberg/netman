# NetMan Agent System

This document describes the agent system used in the NetMan project, including available agent profiles, their responsibilities, and how they integrate with the development workflow.

## Overview

The NetMan project uses a specialized agent system to streamline development across different domains. Agents are specialized AI assistants that focus on specific areas of the codebase, ensuring expertise and consistency in their respective domains.

## Agent Profiles

### 1. Backend Developer Agent

**File**: `.github/agents/backend-developer.agent.md`

**Responsibilities**:
- Writing backend code using Kotlin and Micronaut framework
- Implementing REST API endpoints and business logic
- Writing unit and integration tests for backend services
- Ensuring code quality and adherence to SOLID principles
- Maintaining at least 80% code coverage

**Technologies**:
- Kotlin 2.3
- Micronaut 4.6
- PostgreSQL 17 with Micronaut Data JDBC
- JUnit 5, WireMock, RestAssured
- OpenAPI/Swagger

**Key Characteristics**:
- Uses volatility-based decomposition following iDesign methodology
- Focuses on layered architecture (Api → Managers → Engines → Access)
- Emphasizes null safety and Kotlin idiomatic patterns
- Follows Micronaut best practices for dependency injection and error handling

### 2. DevOps Developer Agent

**File**: `.github/agents/devops-developer.agent.md`

**Responsibilities**:
- Managing CI/CD pipelines using GitHub Actions
- Writing infrastructure as code using Pulumi
- Ensuring infrastructure reliability and scalability
- Providing feedback on infrastructure usage and best practices

**Technologies**:
- GitHub Actions
- Pulumi
- Azure
- Docker

**Key Characteristics**:
- Does not modify application code (backend or frontend)
- Focuses exclusively on infrastructure and deployment automation
- Expert in cloud platforms and containerization
- Ensures CI/CD pipelines are efficient and reliable

### 3. Frontend Developer Agent

**File**: `.github/agents/frontend-developer.agent.md`

**Responsibilities**:
- Building modern web applications using Svelte and SvelteKit
- Implementing user interfaces with TypeScript
- Ensuring accessibility and performance
- Writing unit and E2E tests
- Maintaining code quality and type safety

**Technologies**:
- Svelte 5.0 with Runes
- SvelteKit 2.16
- TypeScript 5.x
- Tailwind CSS 4.0 with DaisyUI 5.1
- Vitest 3.0 (unit tests)
- Playwright 1.49 (E2E tests)
- Hanko Elements 2.1 (authentication)

**Key Characteristics**:
- Uses Svelte 5 runes for reactive state management
- Follows component-based architecture with file-based routing
- Emphasizes type safety and strict TypeScript usage
- Focuses on accessibility and performance best practices
- Uses Tailwind CSS utility classes for styling

## Agent Workflow Integration

### Development Process

1. **Task Assignment**: Tasks are assigned to the most appropriate agent based on the domain
2. **Context Analysis**: Agent analyzes relevant code and documentation
3. **Implementation**: Agent writes code following domain-specific best practices
4. **Testing**: Agent ensures proper test coverage and quality
5. **Review**: Code is reviewed for adherence to standards

### Collaboration Patterns

- **Backend-Frontend Integration**: Agents collaborate on API contracts and data models
- **DevOps Integration**: DevOps agent ensures infrastructure supports application requirements
- **Cross-domain Reviews**: Agents provide feedback on related areas (e.g., backend agent reviews API usage in frontend)

## Agent-Specific Instructions

Each agent has detailed instructions that guide their work:

### Backend Instructions

**File**: `.github/instructions/backend.instructions.md`

Covers:
- Technology stack and dependencies
- Code organization and architecture patterns
- Development commands and workflows
- Kotlin coding standards and best practices
- Micronaut-specific patterns and annotations
- Database access patterns using Micronaut Data
- Testing guidelines and coverage requirements
- API design conventions and OpenAPI documentation
- Security and input validation practices
- Performance considerations and logging standards

### Frontend Instructions

**File**: `.github/instructions/frontend.instructions.md`

Covers:
- Technology stack and build tools
- Code organization and file structure
- Development commands and workflows
- Code style and naming conventions
- Svelte 5 features and runes usage
- SvelteKit patterns and routing
- Styling with Tailwind CSS and DaisyUI
- API integration and environment variables
- Testing strategies (unit and E2E)
- Authentication with Hanko
- Type safety and interface definitions
- Performance and accessibility best practices

## Agent Selection Criteria

### When to Use Each Agent

**Backend Developer Agent**:
- API endpoint implementation
- Business logic development
- Database schema changes
- Backend service integration
- Unit and integration testing

**Frontend Developer Agent**:
- UI component development
- Page layout and routing
- Client-side state management
- Form handling and validation
- Frontend testing (unit and E2E)
- Accessibility improvements

**DevOps Developer Agent**:
- CI/CD pipeline configuration
- Infrastructure provisioning
- Deployment automation
- Containerization and orchestration
- Environment configuration

## Best Practices for Working with Agents

### General Guidelines

1. **Clear Task Definition**: Provide specific, well-defined tasks
2. **Context Provision**: Include relevant code references and documentation
3. **Domain Alignment**: Assign tasks to the appropriate agent
4. **Feedback Loop**: Provide constructive feedback on agent output
5. **Iterative Improvement**: Allow agents to refine their work based on feedback

### Agent-Specific Recommendations

**Backend Agent**:
- Provide API specifications and data models
- Reference existing patterns and architectures
- Specify testing requirements and edge cases
- Highlight performance considerations

**Frontend Agent**:
- Provide design mockups or wireframes
- Specify accessibility requirements
- Define user interaction flows
- Highlight performance constraints

**DevOps Agent**:
- Specify infrastructure requirements
- Define deployment constraints
- Provide security and compliance requirements
- Specify monitoring and logging needs

## Agent Limitations

### What Agents Cannot Do

1. **Cross-domain Work**: Agents focus on their specific domain
2. **Creative Decision Making**: Agents follow established patterns and best practices
3. **Ambiguous Requirements**: Agents need clear, specific instructions
4. **Human Judgment**: Agents cannot replace human code reviews and architectural decisions

### When to Escalate to Human Developers

1. **Architectural Decisions**: Major system design changes
2. **Ambiguous Requirements**: Unclear or conflicting specifications
3. **Complex Integrations**: Multi-system coordination
4. **Production Issues**: Critical bug fixes and incident response

## Agent Performance Optimization

### Maximizing Agent Effectiveness

1. **Clear Documentation**: Well-documented code and architecture
2. **Consistent Patterns**: Established coding conventions
3. **Comprehensive Tests**: Good test coverage for reference
4. **Detailed Instructions**: Specific agent guidance documents
5. **Contextual Information**: Relevant code examples and references

## Future Agent Development

### Potential Agent Enhancements

1. **Specialized Testing Agent**: Focused on test strategy and coverage
2. **Database Migration Agent**: Specialized in schema changes and data migrations
3. **Security Agent**: Focused on security best practices and vulnerability scanning
4. **Performance Agent**: Specialized in optimization and profiling

### Agent Evolution Process

1. **Identify Gaps**: Determine areas needing specialized expertise
2. **Define Scope**: Clearly outline agent responsibilities
3. **Create Instructions**: Develop comprehensive guidance documents
4. **Iterative Improvement**: Refine agent behavior based on feedback

## Conclusion

The NetMan agent system provides specialized expertise across different domains, enabling efficient and consistent development. By leveraging domain-specific agents, the project maintains high code quality, follows best practices, and ensures proper separation of concerns.

For detailed agent-specific guidance, refer to the individual agent definition files and instruction documents in the `.github/agents/` and `.github/instructions/` directories.