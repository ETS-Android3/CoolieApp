import requests
from bs4 import BeautifulSoup
def station_data(num):
    URL = 'https://www.goibibo.com/trains/app/trainstatus/results/?train='+str(num)

    class_list = set()

    page = requests.get( URL )

    soup = BeautifulSoup( page.content , 'html.parser')
    l=[]
    rows = soup.find_all('span')
    flag=False
    for row in rows:         
        x=(row.get_text())
        ch=x.lower()
        if not flag:
            if ("station" in ch and "-" in ch )or ("junction" in ch and "-" in ch)  :

                x=(x.split())
                code=x[0]
                x=' '.join(x[2:])
                if 'railway' in x.lower():
                    x=x.replace('Railway',"") 
                if 'station' in x.lower():
                    x=x.replace('Station',"") 
                fin=code+" "+x
                fin=fin.rstrip()
                fin=fin.lstrip()
                l.append(fin)
        else:
            if x.lower()=="train is running towards this station":
                flag=False
    return l
def bkng(soup, d):
        chart = soup.find(class_="chart-status-txt")
        chart = chart.text
        e = soup.find(class_="boarding-detls")
        e1 = ((e.text.replace("\n", " ")))
        e1 = e1.replace("DAY OF BOARDING", '')
        e1 = e1.replace("CLASS", '')
        a = e1.split("  ")
        d['platform'] = a[-2].split()[-1]
        d['date_of_jrny'] = a[1]
        d["class"] = a[-3]
        d["status"] = chart
        return d
def srcdest(soup, d):

        f = soup.find(class_="train-route")
        a = []
        for i in f.findAll('div', attrs={'class': 'col-xs-4'}):
            l = i.text.replace("\n", " ")
            l = l.replace("FROM", " ")
            l = l.replace("TO", " ")
            a.append(l)
        e = soup.find_all(class_="pnr-bold-txt")
        l1 = []
        for i in e:
            i = i.text
            try:
                i = i.replace("\n", "")
                i = i.replace("  ", "")
                l1.append(i)
            except:
                l1.append(i)
        a1 = a[0].split("|")
        d['src'] = a1[0].replace(" ", "")
        cd = a1[1].split()
        d['src_code'] = cd[0]
        d['start_time'] = cd[1]+" "+cd[2]
        a1 = a[1].split("|")
        d['dst'] = a1[0].replace(" ", "")
        cd = a1[1].split()
        d['dst_code'] = cd[0]
        d['dest_time'] = cd[1]+" "+cd[2]
        d['valid'] = True
        d["train_name"] = l1[2][5:]
        return d
def st_cd(pnr):
    x="{%22pnr%22:%22"+pnr+"%22}"
    URL = "https://www.goibibo.com/trains/view/pnr-result/?hquery="+str(x)

    r = requests.get(URL)

    soup = BeautifulSoup(r.content , 'html.parser')

    name_train = soup.find(class_="heading") 
    return str(name_train.text).split()[0]
def source(clas,d,soup):
        
        chart4 = soup.find(class_=clas) 

        count = 0


        for i in chart4:

            if count == 1:

                temp = i.text

                d['src_code'],d['src'] = temp.split(",")

            elif count == 2:

                d['start_time'] = i.text


            elif count == 3:

                d['date_of_jrny'] = i.text

                

            count += 1

            
def destination(clas,d,soup):
        
        chart5 = soup.find(class_= clas) 

        d['dst_code'],d['dst'] = (chart5.text).split(",")

        chart6 = soup.find(class_="font-weight-bold black-text append-bottom5 right-info-text text-right") 


        d['dest_time'] = chart6.text
def bkng(soup, d):
        chart = soup.find(class_="chart-status-txt")
        chart = chart.text
        e = soup.find(class_="boarding-detls")
        e1 = ((e.text.replace("\n", " ")))
        e1 = e1.replace("DAY OF BOARDING", '')
        e1 = e1.replace("CLASS", '')
        a = e1.split("  ")
        d['platform'] = a[-2].split()[-1]
        d['date_of_jrny'] = a[1]
        d["class"] = a[-3]
        d["status"] = chart
        return d

def srcdest(soup, d):

        f = soup.find(class_="train-route")
        a = []
        for i in f.findAll('div', attrs={'class': 'col-xs-4'}):
            l = i.text.replace("\n", " ")
            l = l.replace("FROM", " ")
            l = l.replace("TO", " ")
            a.append(l)
        e = soup.find_all(class_="pnr-bold-txt")
        l1 = []
        for i in e:
            i = i.text
            try:
                i = i.replace("\n", "")
                i = i.replace("  ", "")
                l1.append(i)
            except:
                l1.append(i)
         
        a1 = a[0].split("|")
        d['src'] = a1[0].replace(" ", "")
        cd = a1[1].split()
        d['src_code'] = cd[0]
        d['start_time'] = cd[1]+" "+cd[2]
        a1 = a[1].split("|")
        d['dst'] = a1[0].replace(" ", "")
        cd = a1[1].split()
        d['dst_code'] = cd[0]
        d['dest_time'] = cd[1]+" "+cd[2]
        d['valid'] = True
        d["train_name"] = l1[2][5:][1:]
        d["seat_status"]=l1[1]
        return d
import requests
from bs4 import BeautifulSoup
import csv
import random 

def train(pnr_number):
    
    
    URL = "https://www.travelkhana.com/travelkhana/PNRresult?pnr="+str(pnr_number)
    
    r = requests.get(URL)
    
    soup = BeautifulSoup(r.content , 'html.parser')
    chart1 = soup.find(class_='dept-tabel PNR-info') 
    chart2 = chart1.findAll('span')
    train_name = ''
    
    for i in range(len(chart2)):
        
        if i == 3:
    
            s = (chart2[i].text).split(" / ")
            
            train_name = s[0]
    
    return train_name 

def pnr_details(pnr_number):
    
    
    URL = "https://www.travelkhana.com/travelkhana/PNRresult?pnr="+str(pnr_number)

    r = requests.get(URL)

    soup = BeautifulSoup(r.content , 'html.parser')

    l=[]

    d = {'valid':True, 
    'src': "", 
    'dst': "", 
    'src_code': "",
    'dst_code': "",
    "start_time": "", 
    "dest_time": "",
    'platform': "", 
    'date_of_jrny': "", 
    'seat_status':"",
    "status": "", 
    "class": "", 
    "train_name": ""
    }

    try:
    
        chart1 = soup.find(class_='dept-tabel PNR-info') 
        chart2 = chart1.findAll('span')

        chart3 = soup.find(class_='show-status') 

        chart4 = chart3.find('h2')

        if 'CHART NOT PREPARED' in chart4.text:

            d['status'] = "Chart Not Prepared"

        else:

            d['status'] = 'Chart Prepared'


        chart5 = chart3.findAll(class_='hidden-xs')


        for i in chart5:


            if 'CNF' in i.text:

                d['seat_status'] = 'Confirmed'

                break 

        if d['seat_status'] != 'Confirmed':

            d['seat_status'] = 'Not Confirmed'


        for i in range(len(chart2)):

            if i == 3:

                s = (chart2[i].text).split(" / ")

                d['train_name'] = s[1]

                pass

            elif i == 5:

                d['src'] = chart2[i].text 

            elif i == 7:

                d['dst'] = chart2[i].text 

            elif i == 9:

                s = ''

                if "," in chart2[i].text:

                    s = (chart2[i].text).split(",")
                else:

                    s = (chart2[i].text).split()

                for i in s:

                    if i and not d['date_of_jrny']:

                        d['date_of_jrny'] = i

                    elif i and not d['start_time']:

                        d['start_time'] = i

                d['dest_time'] = d['start_time']

            elif i == 11:

                d['class'] = chart2[i].text
                
            d['platform'] = str(random.randint(1,4))

    except:
        d['valid'] = False
    
    return d

    


def train_num(pnr):
    url = 'http://www.railyatri.in/pnr-status/' + pnr
    html = requests.get(url)
    soup = BeautifulSoup(html.content, 'html.parser')
    e = soup.find_all(class_="pnr-bold-txt")
    l = []
    for i in e:
        i = i.text
        try:
            i = i.replace("\n", "")
            i = i.replace("  ", "")
            l.append(i)
        except:
            l.append(i)
    num = (l[2][:5])
    return num
