import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';
import { dirname } from 'path';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);
const logFilePath = path.join(__dirname, 'service.log');

function mapStatusText(statusCode) {
  if (!statusCode) return 'UNKNOWN';
  if (statusCode >= 200 && statusCode < 300) return 'SUCCESS';
  if (statusCode >= 400 && statusCode < 500) return 'CLIENT_ERROR';
  if (statusCode >= 500) return 'SERVER_ERROR';
  return 'UNKNOWN';
}

function formatLog({
  level,
  message,
  transactionId,
  serviceId,
  statusCode,
  durationMs,
  userId,
  ip,
  method,
  route,
  data
}) {
  return {
    timestamp: new Date().toISOString(),
    level,
    message,
    transactionId,
    serviceId,
    route,
    method,
    status: statusCode || 'UNKNOWN',
    statusText: mapStatusText(statusCode),
    ...(durationMs !== undefined && { durationMs }),
    ...(userId && { userId }),
    ...(ip && { ip }),
    ...(data && { data }),
  };
}

function log(params) {
  const entry = formatLog(params);
  const json = JSON.stringify(entry);

  if (params.level === 'error') console.error(json);
  else if (params.level === 'warn') console.warn(json);
  else console.log(json);

  // fs.appendFileSync(logFilePath, json + '\n');
}

export const logInfo = (params) => log({ ...params, level: 'info' });
export const logError = (params) => log({ ...params, level: 'error' });
export const logWarn = (params) => log({ ...params, level: 'warn' });
