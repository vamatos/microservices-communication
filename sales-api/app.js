import express from "express";
import { createInitialData } from "./src/config/db/initialData.js";
import { connectMongoDb } from "./src/config/db/mongoDbConfig.js";
import checkToken from "./src/config/auth/checkToken.js";
import { connectRabbitMQ } from "./src/config/rabbitmq/rabbitConfig.js";
import { sendMessageToProductStockUpdateQueue } from "./src/modules/product/rabbitmq/productStockUpdateSender.js";
import orderRoutes from "./src/modules/sales/routes/OrderRoutes.js";
import tracing from "./src/config/middleware/tracing.js";
import { requestLogger } from "./src/config/middleware/requestLogger.js"



const app = express();
const env = process.env.NODE_ENV || "development";
const port = process.env.PORT || 8082;
const HALF_MINUTE = 180000;
const CONTAINER_ENV = "container";

startApplication();

async function startApplication() {
  if (CONTAINER_ENV === env.NODE_ENV) {
    logInfo({ message: 'Waiting for RabbitMQ to be ready...' });
    setInterval(async () => {
      connectMongoDb();
      connectRabbitMQ();

    }, HALF_MINUTE);
  } else {
    connectMongoDb();
    createInitialData();
    connectRabbitMQ();
  }
}



app.use(express.json());

app.use(requestLogger);
app.get("/api/status", async (req, res) => {
  return res.status(200)
    .json(
      {
        service: "Sales-API",
        status: "up",
        httpStatus: 200,
      });
});

app.use(tracing);
app.use(checkToken);
app.use(orderRoutes);

app.get("/api/initial-data", (req, res) => {
  createInitialData();
  return res.status(200)
  .json(
    { service: "sales-API",
      status: "up",
      httpStatus: 200,
      });
});



app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});