import mongoose from "mongoose";
import { MONGO_DB_URL } from "../constants/secrets.js";

export function connectMongoDb(){
  mongoose.connect(MONGO_DB_URL, {
    useNewUrlParser: true,
    serverSelectionTimeoutMS: 180000,
  })
  .then(() => {
    console.log("The application connected to MongoDB successfully");
  })
  .catch((error) => {
    console.error("MongoDB connection error:", error);
  });

}