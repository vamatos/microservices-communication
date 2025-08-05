import { v4 as uuidv4 } from 'uuid';
import { logInfo } from '../logger.js';

export function requestLogger(req, res, next) {
  const transactionid = req.headers['x-transaction-id'];
  const serviceid = req.headers['x-service-id'];
  const start = Date.now();


  const ip = req.headers['x-forwarded-for'] || req.connection.remoteAddress;

  // Log da requisição de entrada
  logInfo({
    transactionid,
    serviceid,
    message: 'Request received',
    ip,
    route: req.originalUrl,
    method: req.method,
    userId: req.user?.id || null,
    data: {
      body: req.body,
      query: req.query,
      headers: req.headers,
    },
  });

  // Intercepta o response
  const originalSend = res.send;
  res.send = function (body) {
    const durationMs = Date.now() - start;

    logInfo({
      transactionid,
      serviceid,
      message: 'Response sent',
      statusCode: res.statusCode,
      ip,
      route: req.originalUrl,
      method: req.method,
      durationMs,
      data: {
        responseBody: safeJsonParse(body),
      },
    });

    res.send = originalSend;
    return res.send(body);
  };

  next();
}

function safeJsonParse(body) {
  try {
    return typeof body === 'string' ? JSON.parse(body) : body;
  } catch {
    return body;
  }
}
