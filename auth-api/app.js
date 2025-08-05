import express from "express";
import * as db from "./src/config/db/initialData.js";
import userRouter from "./src/modules/user/routes/UserRoutes.js";
import tracing from "./src/config/middleware/tracing.js";
import { requestLogger } from './src/config/middleware/requestLogger.js';

const app = express();
const env = process.env.NODE_ENV || "development";
const port = process.env.PORT || 8080;
const CONTAINER_ENV = "container";



app.use(express.json());

startApplication();

function startApplication(){
  if(env.NODE_ENV !== CONTAINER_ENV){
    db.createInitialData();
  }
}

app.get("/api/initial-data", (req, res) => {
  db.createInitialData();
  return res.status(200)
  .json(
    { service: "Auth-API",
      status: "up",
      httpStatus: 200,
      });
});

app.use(tracing);
app.use(requestLogger);
app.use(userRouter);

app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});