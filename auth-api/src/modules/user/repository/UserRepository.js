import User from "../model/User.js";

class UserRepository{

  async findById(id){
      try {
        return await User.findById({where: {id: id}});
      } catch (error) {
        console.error(error.message)
        return null;
      }
  }
  async findByEmail(email){
      try {
        return await User.findOne({where: {email: email}});
      } catch (error) {
        console.error(error.message)
        return null;
      }
  }

}

export default new UserRepository();