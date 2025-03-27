const fs = require('fs');
const path = require('path');

const sourceDirs = {
  asyncapi: path.join(__dirname, '../../generators/packages/generator-asyncapi/src/test/asyncapi-files'),
  openapi: path.join(__dirname, '../../generators/packages/generator-openapi/src/test/openapi-files'),
};

const targetDirs = {
  asyncapi: path.join(__dirname, '../src/test/asyncapi/specs'),
  openapi: path.join(__dirname, '../src/test/openapi/specs'),
};

// Create target directories if they don't exist
Object.values(targetDirs).forEach(dir => {
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }
});

// Copy files for each type
Object.entries(sourceDirs).forEach(([type, sourceDir]) => {
  const files = fs.readdirSync(sourceDir);
  files.forEach(file => {
    const sourcePath = path.join(sourceDir, file);
    const targetPath = path.join(targetDirs[type], file);
    fs.copyFileSync(sourcePath, targetPath);
    console.log(`Copied ${file} to ${type} specs directory`);
  });
}); 