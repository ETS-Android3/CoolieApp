from fastapi import FastAPI
from typing import Optional
from pydantic import BaseModel
import requests
from bs4 import BeautifulSoup
from support import *
app = FastAPI()







@app.get('/')
def index():
    return {"name": "yuvaraj-131"}


@app.post('/pnr')
async def pnr(pnr: str):
    
    d = {'valid': False, 'src': "", 'dst': "", 'src_code': "",
         'dst_code': "", "start_time": "", "seat_status":"","dest_time": "", 'platform': "", 'date_of_jrny': "", "status": "", "class": "", "train_name": ""}

    if len(pnr) > 10 or len(pnr) < 10:
        print(d)
        return {'valid': d}
    try:
        d=pnr_details(pnr)
      
        return {'valid': d}
    except:
        url = 'http://www.railyatri.in/pnr-status/' + pnr
        html = requests.get(url)
        soup = BeautifulSoup(html.content, 'html.parser')
        srcdest(soup, d)
        bkng(soup, d)
        return {'valid': d}




@app.post('/station')
def station(pnr: str):
    if len(pnr) > 10 or len(pnr) < 10:
        return {'valid': False}
    if pnr=="1234567891" or pnr=="2326710214":
            
        l=  [
        
        "Visakhapatnam VSKP",
        "Tirupati Main TPTY",
        "Bangalore SBC",
        "Renigunta Jn RU",
        "Sri Kalahasti KHT",
        "Venkatagiri VKI",
        "Gudur Jn GDR",
        "Nellore NLR",
        "Kavali KVZ",
        "Singarayakonda SKM",
        "Ongole OGL",
        "Chirala CLX",
        "Bapatla BPP",
        "Tenali Jn TEL",
        "Guntur Jn GNT",
        "Sattenapalle SAP",
        "Piduguralla PGRL",
        "Nadikode NDKD",
        "Vishnupuram VNUP",
        "Miryalaguda MRGA",
        "Nalgonda NLDA",
        "Chityala CTYL",
        "Ramannapet RMNP",
        "Bibinagar BN",
        "Secunderabad Jn SC",
        "Begampet BMT",
        "Lingampalli LPI"
    ]

        return {'valid': True, 'output': l}

    try:
        
        try:
            num=train(pnr)
        except:
            num=train_num(pnr)
        print(num)
        l=[]
        if pnr=="1234567891" or pnr=="2326710214":
            URL ="https://www.trainman.in/running-status/"+"12733"
        else:
            URL ="https://www.trainman.in/running-status/"+str(num)
        print(URL)
        page = requests.get( URL )
        soup = BeautifulSoup( page.content , 'html.parser')
        print(soup)
        e = soup.find_all(class_="st-name")
        print(e)
        for row in e:         
                x=(row.get_text())
                x=x.split('(')
                cd=x[0]+x[-1][:-1]
                l.append(cd)

        return {'valid': True, 'output': l}
    except:

        return {'valid': False}
