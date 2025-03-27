import { describe, it, expect } from 'vitest';
import { TestConfig, PluginConfig, createTestUtils } from '../setup';

export const createDomainTests = (config: TestConfig, plugin: (config: any, options: PluginConfig) => Promise<void>) => {
  describe('domains', () => {
    it('creates domain if it does not exist', async () => {
      const utils = createTestUtils(config);
      const { getDomain } = utils;

      await plugin(config, {
        services: [{ path: utils.joinPath('simple.yml'), id: 'test-service' }],
        domain: { id: 'orders', name: 'Orders Domain', version: '1.0.0' },
      });

      const domain = await getDomain('orders', '1.0.0');
      expect(domain).toEqual(
        expect.objectContaining({
          id: 'orders',
          name: 'Orders Domain',
          version: '1.0.0',
          services: [{ id: 'test-service', version: '1.0.0' }],
        })
      );
    });

    it('handles domain versioning correctly', async () => {
      const utils = createTestUtils(config);
      const { writeDomain, getDomain } = utils;

      await writeDomain({
        id: 'orders',
        name: 'Orders Domain',
        version: '0.0.1',
        markdown: '',
      });

      await plugin(config, {
        services: [{ path: utils.joinPath('simple.yml'), id: 'test-service' }],
        domain: { id: 'orders', name: 'Orders Domain', version: '1.0.0' },
      });

      const versionedDomain = await getDomain('orders', '0.0.1');
      const newDomain = await getDomain('orders', '1.0.0');

      expect(versionedDomain.version).toEqual('0.0.1');
      expect(newDomain.version).toEqual('1.0.0');
      expect(newDomain.services).toEqual([{ id: 'test-service', version: '1.0.0' }]);
    });

    it('adds services to existing domain', async () => {
      const utils = createTestUtils(config);
      const { writeDomain, getDomain } = utils;

      await writeDomain({
        id: 'orders',
        name: 'Orders Domain',
        version: '1.0.0',
        markdown: '',
      });

      await plugin(config, {
        services: [{ path: utils.joinPath('simple.yml'), id: 'test-service' }],
        domain: { id: 'orders', name: 'Orders Domain', version: '1.0.0' },
      });

      const domain = await getDomain('orders', '1.0.0');
      expect(domain.services).toEqual([{ id: 'test-service', version: '1.0.0' }]);
    });

    it('handles multiple services in domain', async () => {
      const utils = createTestUtils(config);
      const { getDomain } = utils;

      await plugin(config, {
        services: [
          { path: utils.joinPath('simple.yml'), id: 'service-1' },
          { path: utils.joinPath('simple.yml'), id: 'service-2' },
        ],
        domain: { id: 'orders', name: 'Orders', version: '1.0.0' },
      });

      const domain = await getDomain('orders', 'latest');
      expect(domain.services).toHaveLength(2);
      expect(domain.services).toEqual([
        { id: 'service-1', version: '1.0.0' },
        { id: 'service-2', version: '1.0.0' },
      ]);
    });

    it('preserves domain owners', async () => {
      const utils = createTestUtils(config);
      const { getDomain } = utils;

      await plugin(config, {
        services: [{ path: utils.joinPath('simple.yml'), id: 'test-service' }],
        domain: { 
          id: 'orders', 
          name: 'Orders', 
          version: '1.0.0',
          owners: ['John Doe', 'Jane Doe']
        },
      });

      const domain = await getDomain('orders', '1.0.0');
      expect(domain.owners).toEqual(['John Doe', 'Jane Doe']);
    });

    it('does not add service to domain when domain is not specified', async () => {
      const utils = createTestUtils(config);
      const { getDomain } = utils;

      await plugin(config, {
        services: [{ path: utils.joinPath('simple.yml'), id: 'test-service' }],
      });

      expect(await getDomain('orders', '1.0.0')).toBeUndefined();
    });
  });
}; 