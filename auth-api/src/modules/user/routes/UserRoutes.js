import { Router } from "express";

import userController from "../controller/userController.js";
import checkToken from "../../../config/auth/checkToken.js";

const userRouter = Router();

userRouter.post('/api/auth/', userController.getAccessToken);

userRouter.use(checkToken);
userRouter.get('/api/user/email/:email', userController.findByEmail);

export default userRouter;