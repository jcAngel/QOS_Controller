#cat addflow.xml | curl -u admin:admin -X PUT -H 'Content-type: text/xml' -d @- http://127.0.0.1:8181/restconf/config/opendaylight-inventory:nodes/node/openflow:14721744650432675840/table/0/flow/3
cat addflow2.xml | curl -u admin:admin -X PUT -H 'Content-type: text/xml' -d @- http://127.0.0.1:8181/restconf/config/opendaylight-inventory:nodes/node/openflow:14721744650432675840/table/0/flow/1
