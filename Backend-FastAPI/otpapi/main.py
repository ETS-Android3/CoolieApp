
from fastapi import FastAPI,Request,Body
from typing import Optional
from pydantic import BaseModel
from support import trans,token,mod_str
from data import sts
from twilio.rest import Client
from fastapi.middleware.cors import CORSMiddleware
import requests
import json
app = FastAPI()
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

def sms(num,msg):
    import requests
    num1="+91"+str(num)
    msg1=str(msg)
    url = "https://rapidapi.rmlconnect.net:9443/bulksms/bulksms?username=rapid-QzrQ6844210000&password=617bf299245383001100f870&type=0&dlr=0&destination="+str(num1)+"&source=RMLPRD&message="+str(msg1)

    payload={}
    headers = {}

    response = requests.request("GET", url, headers=headers, data=payload)

    print(response.text,"sms")
def whatsap(num,msg):
    import requests
    import json

    url = "https://rapidapi.rmlconnect.net/wbm/v1/message"

    payload = json.dumps({
    "phone": "+91"+str(num),
    "text": str(msg),
    
    })
    headers = {
    'Content-Type': 'application/json',
    'Authorization': '617bf299245383001100f870'
    }

    response = requests.request("POST", url, headers=headers, data=payload)

    print(response.text,"whatsapp")

@app.get('/')
def index():
    return sts()
@app.post('/otp')
def otpapp(out: dict = Body(...)):
   
  #  out=json.loads(out, strict=False)
    #"coolie/wheelchair, coolie_no, message1, user_no, message2"
    if out["0"]!="wheelchair":
        
        out["2"]=mod_str(out["2"],out["8"])#
        out["4"]=mod_str(out["4"],out["8"])

        a="Welcome to COOLIE APP\n"+out["2"]
        sms(str(out['1']),str(a))
        if str(out['5'])!="":
            token(str(out['5']),"Received New Order!","Please Check Orders Sections")
        whatsap(str(out['1']),str(a))
         
        a="Welcome to COOLIE APP\n"+out["4"]
        sms(str(out['3']),str(a))
        if str(out['6'])!="":
            token(str(out['6']),"Order Successful","For more detials please check All Bokings Section")
        whatsap(str(out['3']),str(a))
        return "SUCESS"
        
    else:
        url = "https://www.fast2sms.com/dev/bulkV2"
        a="Welcome to COOLIE APP\n"+out["4"]
         
        payload = "sender_id=FSTSMS&message=+"+str(a)+"&language=english&route=V3&numbers="+str(out["3"])
        headers = {
            'authorization': "ltfmzHSvcZKQ32OM90py4i5TDaoksXdC87RxLVWbBFNrnuYUGJDbwGWl6IVhxPHutdAC1XnOf8ENg0aF",
            'Content-Type': "application/x-www-form-urlencoded",
            'Cache-Control': "no-cache",
        }
        response = requests.request("POST", url, data=payload, headers=headers)
         
        return "SUCESS"
""""
    @app.post('/otp')
    def otpapp(out: dict = Body(...)):
    
    #  out=json.loads(out, strict=False)
        #"coolie/wheelchair, coolie_no, message1, user_no, message2"
        if out["0"]!="wheelchair":
            account_sid ="AC7c9ab9251269d1aa388a5a5772e3b997"
            auth_token ="84e74ba05be60a30c9874232154ad1f5"
            client = Client(account_sid, auth_token)
            
            out["2"]=mod_str(out["2"],out["8"])#
            out["4"]=mod_str(out["4"],out["8"])

            a="Welcome to COOLIE APP\n"+out["2"]
            url = "https://www.fast2sms.com/dev/bulkV2"
            payload = "sender_id=TXTIND&message="+str(a)+"&route=v3&numbers="+str(out['1'])
            headers = {
                'authorization': "ltfmzHSvcZKQ32OM90py4i5TDaoksXdC87RxLVWbBFNrnuYUGJDbwGWl6IVhxPHutdAC1XnOf8ENg0aF",
                'Content-Type': "application/x-www-form-urlencoded",
                'Cache-Control': "no-cache",
                }
            response = requests.request("POST", url, data=payload.encode('utf-8'), headers=headers)
            if str(out['5'])!="":
                token(str(out['5']),"Received New Order!","Please Check Orders Sections")
            num="whatsapp:"+"+91"+str(out['1'])
            message = client.messages \
                            .create(
                                from_='whatsapp:+14155238886',
                                body=str(a),
                                to=num
                            )
            print(message.sid)
            a="Welcome to COOLIE APP\n"+out["4"]
            payload = "sender_id=TXTIND&message="+str(a)+"&route=v3&numbers="+str(out['3'])
            response = requests.request("POST", url, data=payload.encode('utf-8'), headers=headers)
            print(response)
            if str(out['6'])!="":
                token(str(out['6']),"Order Successful","For more detials please check All Bokings Section")
            num="whatsapp:"+"+91"+str(out['3'])
            message = client.messages \
                            .create(
                                from_='whatsapp:+14155238886',
                                body=str(a),
                                to=num
                            )


            print(message.sid)
            return "SUCESS"
            
        else:
            url = "https://www.fast2sms.com/dev/bulkV2"
            a="Welcome to COOLIE APP\n"+out["4"]
            
            payload = "sender_id=FSTSMS&message=+"+str(a)+"&language=english&route=V3&numbers="+str(out["3"])
            headers = {
                'authorization': "ltfmzHSvcZKQ32OM90py4i5TDaoksXdC87RxLVWbBFNrnuYUGJDbwGWl6IVhxPHutdAC1XnOf8ENg0aF",
                'Content-Type': "application/x-www-form-urlencoded",
                'Cache-Control': "no-cache",
            }
            response = requests.request("POST", url, data=payload, headers=headers)
            
            return "SUCESS"
"""
@app.get('/out')
def otpapp(out: str):
    import requests
    import random

    # We create a set of digits: {0, 1, .... 9}
    digits = set(range(10))
    # We generate a random integer, 1 <= first <= 9
    first = random.randint(1, 9)
    # We remove it from our set, then take a sample of
    # 3 distinct elements from the remaining values
    last_3 = random.sample(digits - {first}, 3)
    ans=(str(first) + ''.join(map(str, last_3)))
    url = "https://www.fast2sms.com/dev/bulk"
    s = "\n\nHello,"+'\n\n'+ "Your OTP is " +str(ans)+ '\n\n' + "Team Oureye.ai"
    payload = "sender_id=FSTSMS&message=+"+str(s)+"&language=english&route=p&numbers="+str(out)
    headers = {
                'authorization': "YUkVJPidmTKvFqNog6E4pu8A7S5z1OtclIDfLajWyehbBMRxnG1y0v3sDmQodwXBUnxFLWR7OgVclujK",
                'Content-Type': "application/x-www-form-urlencoded",
                'Cache-Control': "no-cache",
            }
    response = requests.request("POST", url, data=payload, headers=headers)
    print(response)
    return {"otp":str(ans)}
 
    



