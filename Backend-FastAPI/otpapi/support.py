import requests
def trans(txt,tr):
    if tr!="en":
        txt1=txt.encode()
        url = "https://google-translate1.p.rapidapi.com/language/translate/v2"

        payload ="q="+str(txt1)+"&format=text"+"&target="+str(tr)+"&source=en"
    #    "q=Hello%2C%20world&format=text&target=hi&source=en"
        headers = {
            'content-type': "application/x-www-form-urlencoded",
            'accept-encoding': "application/gzip",
            'x-rapidapi-host': "google-translate1.p.rapidapi.com",
            'x-rapidapi-key': "f124debcb6mshc4b633fb8d3ea3ep139069jsneb2a50f4b06a"
            }

        response = requests.request("POST", url, data=payload, headers=headers)
        response= response.json() 
        print(response)
        try :
            return response['data']['translations'][0]['translatedText'] 
        except:
            return txt  
    return txt

def token(key,title,body):
    

    API_ENDPOINT = "https://fcm.googleapis.com/fcm/send"
    header={"Content-Type": "application/json",
    "Authorization": "key=AAAA-80VN-c:APA91bEOvr_xPw-725fhcnM2gD3Uki2B2GVWQAelKxRDIr2M-tMKP6IyXAPw7ZwfRHXEms20a_e8iIb9FLEBO1GLRGdr40TB2qPUAjhQ_7b-xCU7SgyHmXx3UjhTV7ggk-hzNTGJWqrE"}
    data = {
    "to": str(key),
    "notification": {
        "title":title,
        "body":body,
        "mutable_content": True,
        "sound": "Tri-tone"
        },

    }
 
    response = requests.post(API_ENDPOINT, headers=header, json=data)


def mod_str(txt,code):
    txt=trans(txt,code)
    txt=txt.replace(".","\n")
    return txt