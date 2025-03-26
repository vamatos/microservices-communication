import jwt from "jsonwebtoken";
import { promisify } from "util";
import AuthException from "./AuthException.js";
import * as secrets from "../constants/secrets.js";
import * as httpStatus from "../constants/httpStatus.js";


const bearer = "bearer ";

export default async (req, res, next) => {
  try {
    const { authorization } = req.headers;
    if (!authorization) {
      throw new AuthException("Token not provided", httpStatus.UNAUTHORIZED);
    }

    let accessToken = authorization
    if (accessToken.includes(bearer)) {
      accessToken = accessToken.replace(bearer, "");
    }

    const decoded = await promisify(jwt.verify)(accessToken, secrets.API_SECRET);
    req.authUser = decoded.authUser;
    return next();
  } catch (error) {
    return res.status(error?.status ? error.status : httpStatus.INTERNAL_SERVER_ERROR).json({ message: error.message });
  }

};