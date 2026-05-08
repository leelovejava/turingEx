const major = Number(process.versions.node.split(".")[0]);
const required = 16;

if (major !== required) {
  console.error(
    `Unsupported Node.js version: ${process.version}. This project requires Node ${required}.x (engines.node=16.20.2).`
  );
  console.error("Please switch Node version, for example: nvm use 16.20.2");
  process.exit(1);
}
