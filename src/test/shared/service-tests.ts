import { describe, it, expect } from 'vitest';
import { TestConfig, PluginConfig, createTestUtils } from '../setup';

export const createServiceTests = (config: TestConfig, plugin: (config: any, options: PluginConfig) => Promise<void>) => {
  describe('services', () => {
    it('creates new service from spec', async () => {
      const utils = createTestUtils(config);
      const { getService } = utils;

      await plugin(config, {
        services: [{ path: utils.joinPath('simple.yml'), id: 'test-service' }],
      });

      const service = await getService('test-service');
      expect(service).toEqual(
        expect.objectContaining({
          id: 'test-service',
          name: expect.any(String),
          version: expect.any(String),
          summary: expect.any(String),
          badges: expect.arrayContaining([
            expect.objectContaining({
              content: expect.any(String),
              textColor: 'blue',
              backgroundColor: 'blue',
            }),
          ]),
        })
      );
    });

    it('updates existing service metadata', async () => {
      const utils = createTestUtils(config);
      const { writeService, getService } = utils;

      await writeService({
        id: 'test-service',
        version: '1.0.0',
        name: 'Random Name',
        markdown: '',
      });

      await plugin(config, {
        services: [{ path: utils.joinPath('simple.yml'), id: 'test-service' }],
      });

      const service = await getService('test-service', '1.0.0');
      expect(service).toEqual(
        expect.objectContaining({
          id: 'test-service',
          name: expect.any(String),
          version: '1.0.0',
          summary: expect.any(String),
          badges: expect.arrayContaining([
            expect.objectContaining({
              content: expect.any(String),
              textColor: 'blue',
              backgroundColor: 'blue',
            }),
          ]),
        })
      );
    });

    it('preserves existing markdown', async () => {
      const utils = createTestUtils(config);
      const { writeService, getService } = utils;

      const originalMarkdown = '# Original Markdown';
      await writeService({
        id: 'test-service',
        version: '1.0.0',
        name: 'Random Name',
        markdown: originalMarkdown,
      });

      await plugin(config, {
        services: [{ path: utils.joinPath('simple.yml'), id: 'test-service' }],
      });

      const service = await getService('test-service', '1.0.0');
      expect(service.markdown).toBe(originalMarkdown);
    });

    it('generates correct badges from spec', async () => {
      const utils = createTestUtils(config);
      const { getService } = utils;

      await plugin(config, {
        services: [{ path: utils.joinPath('simple.yml'), id: 'test-service' }],
      });

      const service = await getService('test-service');
      expect(service.badges).toEqual(
        expect.arrayContaining([
          expect.objectContaining({
            content: expect.any(String),
            textColor: 'blue',
            backgroundColor: 'blue',
          }),
        ])
      );
    });

    it('handles service with no operationIds', async () => {
      const utils = createTestUtils(config);
      const { getService } = utils;

      await plugin(config, {
        services: [{ path: utils.joinPath('without-operationIds.yml'), id: 'test-service' }],
      });

      const service = await getService('test-service');
      expect(service).toBeDefined();
      expect(service.name).toBeDefined();
    });

    it('handles circular references in spec', async () => {
      const utils = createTestUtils(config);
      const { getService } = utils;

      await plugin(config, {
        services: [{ path: utils.joinPath('circular-ref.yml'), id: 'test-service' }],
      });

      const service = await getService('test-service');
      expect(service).toBeDefined();
      expect(service.name).toBeDefined();
    });
  });
}; 