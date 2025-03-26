import UserService from "../service/userService.js";


class UserController {

  async getAccessToken(req, res) {
    let accessToken = await UserService.getAccessToken(req);
    return res.status(accessToken.status).json(accessToken);
  }


  async findByEmail(req, res) {
    try {
      let user = await UserService.findByEmail(req);
      return res.status(user.status).json(user);
    } catch (error) {
      console.error(error.message)
      return res.status(500).json({ message: "Internal Server Error" });
    }
  }

  
}


export default new UserController();