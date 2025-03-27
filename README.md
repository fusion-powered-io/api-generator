# API Generator

A unified API generator that supports both OpenAPI and AsyncAPI specifications. This tool allows you to import and generate documentation from both types of API specifications simultaneously.

## Features

- Support for OpenAPI (Swagger) specifications
- Support for AsyncAPI specifications
- Unified interface for handling both API types
- MIT licensed for maximum flexibility

## Installation

```bash
npm install @eventcatalog/api-generator
```

## Usage

This generator is designed to be used with EventCatalog. Add it to your `eventcatalog.config.js` file:

```js
/** @type {import('@eventcatalog/core').Config} */
export default {
  // ... other config options ...
  generators: [
    [
      '@eventcatalog/api-generator',
      {
        services: [
          {
            id: 'orders-service',
            name: 'Orders Service',
            version: '1.0.0',
            // Optional OpenAPI spec
            openapi: {
              path: path.join(__dirname, 'specs', 'orders-service.openapi.yml')
            },
            // Optional AsyncAPI spec
            asyncapi: {
              path: path.join(__dirname, 'specs', 'orders-service.asyncapi.yml')
            }
          }
        ],
        domain: { 
          id: 'orders', 
          name: 'Orders Domain', 
          version: '1.0.0' 
        }
      }
    ]
  ]
};
```

The generator will:
1. Read the OpenAPI and/or AsyncAPI files for each service
2. Generate services and messages in your catalog
3. Add the services to the specified domain
4. Handle versioning automatically

## Development

```bash
# Install dependencies
npm install

# Run tests
npm test

# Build the project
npm run build

# Run in development mode
npm run dev
```

## License

MIT
