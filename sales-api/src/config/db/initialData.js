import Order from "../../modules/sales/model/Order.js";
import { v4 as uuid4 } from 'uuid';
export async function createInitialData(){
  await Order.collection.drop();
  let firstOrder = await Order.create({
    products:[
      {
        productId: "1001",
        quantity: 2,
      },
      {
        productId: "1002",
        quantity: 1,
      },
      {
        productId: "1003",
        quantity: 1,
      }
    ],
    user:{
      id: 'asasas1212saasasa',
      name: 'Vinicius Matos',
      email: '14viniciusandre@gmail.com',
    },
    status: 'APPROVED',
    createdAt: new Date(),
    updatedAt: new Date(),
    transactionid: uuid4(),
    serviceid: uuid4(),
  });
  let secondOrder = await Order.create({
    products:[
      {
        productId: "1001",
        quantity: 4,
      },
      {
        productId: "1003",
        quantity: 2,
      },
    ],
    user:{
      id: 'asasas1212saasasaddsds',
      name: 'Vinicius Matos 2',
      email: '14viniciusandre2@gmail.com',
    },
    status: 'REJECTED',
    createdAt: new Date(),
    updatedAt: new Date(),
    transactionid: uuid4(),
    serviceid: uuid4(),
  });

  let initialData = await Order.find();
}