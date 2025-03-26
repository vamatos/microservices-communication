import express from "express";
import * as db from "./src/config/db/initialData.js";
import userRouter from "./src/modules/user/routes/userRoutes.js";

const app = express();
const env = process.env.NODE_ENV || "development";
const port = process.env.PORT || 8080;

db.createInitialData();

app.use(express.json());

app.get("/api/status", (req, res) => {
  return res.status(200)
  .json(
    { service: "Auth-API",
      status: "up",
      httpStatus: 200,
      });
});

app.use(userRouter);


app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});