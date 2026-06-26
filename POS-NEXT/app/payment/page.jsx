"use client"

import { FetchEntity } from "@/apicalls/fetch/FetchEntity";
import { PiggyBankIcon } from "lucide-react";

export default function Payment(){

    const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080/api";

    const customerDto = {
        "customerId": "pranav@gmail.com",
        "paymentType": "UPI"
    }
    const handleOrders = async () => {

        const postOrderRes = await FetchEntity(`${baseUrl}/orders/add`,"POST", customerDto)        
        if(postOrderRes==null){
            return;
        }
    }

    return(
        <>
        <div>
        <h1 className="text-center text-2xl font-bold m-7 p-5">
            Payment Page<PiggyBankIcon size={50} className="m-auto"/>
        </h1>
        </div>
        <div className="flex justify-center">
            <button onClick={handleOrders} className="bg-violet-600 text-white rounded-2xl p-4">Payment Successfull</button>
        </div>
        </>
    )
}