import jwt from "jsonwebtoken";
import { promisify } from "util";
import AuthException from "./AuthException.js";
import {API_SECRET} from "../constants/secrets.js";
import { UNAUTHORIZED, INTERNAL_SERVER_ERROR } from "../constants/httpStatus.js";


const bearer = "bearer ";

export default async (req, res, next) => {
  try {
    const { authorization } = req.headers;
    if (!authorization) {
      throw new AuthException("Token not provided", UNAUTHORIZED);
    }

    let accessToken = authorization
    if (accessToken.includes(bearer)) {
      accessToken = accessToken.replace(bearer, "");
    }

    const decoded = await promisify(jwt.verify)(accessToken, API_SECRET);
    req.authUser = decoded;
    return next();
  } catch (error) {
    return res.status(error?.status ? error.status : INTERNAL_SERVER_ERROR).json({ message: error.message });
  }

};