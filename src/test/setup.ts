import { expect, it, describe, beforeEach, afterEach } from 'vitest';
import { join } from 'node:path';
import * as fs from 'fs/promises';
import { existsSync } from 'fs';
import utils from '@eventcatalog/sdk';

export interface TestConfig {
  catalogDir: string;
  examplesDir: string;
}

export const setupTestEnvironment = async (examplesDir: string): Promise<TestConfig> => {
  const catalogDir = join(__dirname, 'catalog');
  if (existsSync(catalogDir)) {
    await fs.rm(catalogDir, { recursive: true });
  }
  await fs.mkdir(catalogDir, { recursive: true });
  process.env.PROJECT_DIR = catalogDir;
  return { catalogDir, examplesDir };
};

export const cleanupTestEnvironment = async (catalogDir: string) => {
  if (existsSync(catalogDir)) {
    await fs.rm(join(catalogDir), { recursive: true });
  }
};

export const createTestUtils = (config: TestConfig) => {
  const { catalogDir, examplesDir } = config;
  const sdk = utils(catalogDir);

  return {
    ...sdk,
    joinPath: (path: string) => join(examplesDir, path),
  };
};

// Shared test types
export interface ServiceTest {
  id: string;
  path: string;
}

export interface DomainTest {
  id: string;
  name: string;
  version: string;
  owners?: string[];
}

export interface PluginConfig {
  services: ServiceTest[];
  domain?: DomainTest;
} 