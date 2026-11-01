from pathlib import Path
import subprocess,urllib.request,urllib.error,json,time,uuid
root=Path(__file__).resolve().parents[1]
log=open(root/'backend/target/smoke-server.log','w')
p=subprocess.Popen(['java','-jar',str(root/'backend/target/retailflow-1.0.0.jar'),'--server.port=18080'],stdout=log,stderr=log)
def call(path,method='GET',body=None,key=None):
 h={'Content-Type':'application/json'}
 if key:h['Idempotency-Key']=key
 req=urllib.request.Request('http://localhost:18080'+path,data=json.dumps(body).encode() if body is not None else None,headers=h,method=method)
 with urllib.request.urlopen(req,timeout=10) as res:return json.load(res)
try:
 for _ in range(45):
  try:
   if call('/actuator/health')['status']=='UP':break
  except (OSError,ValueError):time.sleep(0.5)
 else:raise AssertionError('Server did not start')
 html=urllib.request.urlopen('http://localhost:18080').read().decode();assert '<app-root>' in html
 products=call('/api/products');assert len(products)==8
 item=products[0];key=str(uuid.uuid4());body={'retailer':'HTTP Smoke Market','items':[{'productId':item['id'],'quantity':2}]}
 a=call('/api/orders','POST',body,key);b=call('/api/orders','POST',body,key);assert a['id']==b['id']
 assert call('/api/products')[0]['stock']==item['stock']-2
 assert call('/api/orders')[0]['lines'][0]['quantity']==2
 c=call('/api/orders/'+a['id']+'/status','PATCH',{'status':'CANCELLED'});assert len(c['events'])==2
 assert call('/api/products')[0]['stock']==item['stock']
 assert call('/api/dashboard')['bookedValue']==0
 print('PASS: executable JAR startup, Angular root, health, catalog, create/replay, stock, persisted order serialization, cancellation and dashboard.')
finally:
 p.terminate();p.wait(timeout=15);log.close()
