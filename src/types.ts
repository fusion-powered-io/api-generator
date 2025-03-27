import { OpenAPIV3_1 } from 'openapi-types';

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

export interface Badge {
  content: string;
  textColor: string;
  backgroundColor: string;
}

export interface Service {
  id: string;
  name: string;
  version: string;
  summary: string;
  badges: Badge[];
  markdown?: string;
}

export interface Domain {
  id: string;
  name: string;
  version: string;
  services: { id: string; version: string }[];
  owners?: string[];
  markdown?: string;
}

export type Operation = {
  path: string;
  method: string;
  operationId: string;
  summary?: string;
  description?: string;
  type: string;
  action: string;
  externalDocs?: OpenAPIV3_1.ExternalDocumentationObject;
  tags: string[];
  extensions?: {
    [key: string]: any;
  };
};

export interface OpenAPIParameter {
  name: string;
  in: string;
  required?: boolean;
  schema?: any;
  description?: string;
}

export interface OpenAPIOperation {
  operationId?: string;
  parameters?: OpenAPIParameter[];
  requestBody?: {
    content?: {
      [contentType: string]: {
        schema: any;
      };
    };
  };
  responses?: {
    [statusCode: string]: {
      isSchema?: boolean;
      content?: {
        [contentType: string]: {
          schema: any;
        };
      };
    };
  };
}

export interface OpenAPIPathItem {
  [method: string]: OpenAPIOperation;
}

export interface OpenAPIPaths {
  [path: string]: OpenAPIPathItem;
}

export interface OpenAPIDocument {
  paths: OpenAPIPaths;
}
