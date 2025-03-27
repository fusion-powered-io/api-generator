import { beforeEach, afterEach, describe, it, expect } from 'vitest';
import { join } from 'node:path';
import { setupTestEnvironment, cleanupTestEnvironment, createTestUtils } from '../setup';
import { createDomainTests } from '../shared/domain-tests';
import { createServiceTests } from '../shared/service-tests';
import plugin from '../../plugins/asyncapi';

const examplesDir = join(__dirname, 'specs');

describe('AsyncAPI Plugin', () => {
  let config: { catalogDir: string; examplesDir: string };

  beforeEach(async () => {
    config = await setupTestEnvironment(examplesDir);
  });

  afterEach(async () => {
    await cleanupTestEnvironment(config.catalogDir);
  });

  // Run shared domain tests
  createDomainTests(config, plugin);

  // Run shared service tests
  createServiceTests(config, plugin);

  // AsyncAPI-specific tests
  describe('AsyncAPI specific features', () => {
    it('handles AsyncAPI message schemas', async () => {
      const utils = createTestUtils(config);
      const { getService } = utils;

      await plugin(config, {
        services: [{ path: utils.joinPath('message-schema.yaml'), id: 'test-service' }],
      });

      const service = await getService('test-service');
      expect(service).toBeDefined();
      expect(service.name).toBeDefined();
    });

    it('handles AsyncAPI with Avro schemas', async () => {
      const utils = createTestUtils(config);
      const { getService } = utils;

      await plugin(config, {
        services: [{ path: utils.joinPath('asyncapi-with-avro.asyncapi.yml'), id: 'test-service' }],
      });

      const service = await getService('test-service');
      expect(service).toBeDefined();
      expect(service.name).toBeDefined();
    });

    it('handles AsyncAPI references', async () => {
      const utils = createTestUtils(config);
      const { getService } = utils;

      await plugin(config, {
        services: [{ path: utils.joinPath('ref-example.asyncapi.yml'), id: 'test-service' }],
      });

      const service = await getService('test-service');
      expect(service).toBeDefined();
      expect(service.name).toBeDefined();
    });

    it('handles AsyncAPI without payload', async () => {
      const utils = createTestUtils(config);
      const { getService } = utils;

      await plugin(config, {
        services: [{ path: utils.joinPath('asyncapi-without-payload.yml'), id: 'test-service' }],
      });

      const service = await getService('test-service');
      expect(service).toBeDefined();
      expect(service.name).toBeDefined();
    });
  });
}); 