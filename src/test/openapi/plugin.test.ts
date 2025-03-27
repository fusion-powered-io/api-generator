import { beforeEach, afterEach, describe, it, expect } from 'vitest';
import { join } from 'node:path';
import { setupTestEnvironment, cleanupTestEnvironment, createTestUtils } from '../setup';
import { createDomainTests } from '../shared/domain-tests';
import { createServiceTests } from '../shared/service-tests';
import plugin from '../../plugins/openapi';

const examplesDir = join(__dirname, 'specs');

describe('OpenAPI Plugin', () => {
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

  // OpenAPI-specific tests
  describe('OpenAPI specific features', () => {
    it('handles OpenAPI references', async () => {
      const utils = createTestUtils(config);
      const { getService } = utils;

      await plugin(config, {
        services: [{ path: utils.joinPath('ref-example.yml'), id: 'test-service' }],
      });

      const service = await getService('test-service');
      expect(service).toBeDefined();
      expect(service.name).toBeDefined();
    });

    it('handles OpenAPI with resolved references', async () => {
      const utils = createTestUtils(config);
      const { getService } = utils;

      await plugin(config, {
        services: [{ path: utils.joinPath('ref-example-with-resolved-refs.yml'), id: 'test-service' }],
      });

      const service = await getService('test-service');
      expect(service).toBeDefined();
      expect(service.name).toBeDefined();
    });

    it('handles OpenAPI in JSON format', async () => {
      const utils = createTestUtils(config);
      const { getService } = utils;

      await plugin(config, {
        services: [{ path: utils.joinPath('ref-example.json'), id: 'test-service' }],
      });

      const service = await getService('test-service');
      expect(service).toBeDefined();
      expect(service.name).toBeDefined();
    });

    it('handles OpenAPI with signup message', async () => {
      const utils = createTestUtils(config);
      const { getService } = utils;

      await plugin(config, {
        services: [{ path: utils.joinPath('ref-example-signup-message.yml'), id: 'test-service' }],
      });

      const service = await getService('test-service');
      expect(service).toBeDefined();
      expect(service.name).toBeDefined();
    });
  });
}); 