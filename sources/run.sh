#curl -u admin:admin -X GET http://localhost:8181/controller/nb/v2/flowprogrammer/default
#curl -u admin:admin -H 'Accept: application/xml' http://localhost:8080/controller/nb/v2/flowprogrammer/default/node/OF/00:03:38:63:bb:59:e2:c0
#echo "Nodes done"
#echo "Meter Start"
cat testflow.xml | curl -u admin:admin -X PUT -H 'Content-type: text/xml' -d @- http://127.0.0.1:8181/restconf/config/opendaylight-inventory:nodes/node/openflow:14721744650432675840/meter/3
#curl -u admin:admin -X DELETE -H 'Content-type: text/xml' http://127.0.0.1:8080/restconf/config/opendaylight-inventory:nodes/node/openflow:14721744650432675840/meter/3
#cat bindflow.xml | curl -u admin:admin -X PUT -H 'Content-type: application/xml' -d @- http://localhost:8080/controller/nb/v2/flowprogrammer/default/node/OF/00:03:38:63:bb:59:e2:c0/staticFlow/flow2
#curl -u admin:admin -X GET http://127.0.0.1:8181/restconf/operational/opendaylight-inventory:nodes/node/openflow:14721744650432675840/meter/3

#cat arpAdd.xml | curl -u admin:admin -X PUT -H 'Content-type: text/xml' -d @- http://127.0.0.1:8181/restconf/config/opendaylight-inventory:nodes/node/openflow:14721744650432675840/table/0/flow/5
#cat bindflow.xml | curl -u admin:admin -X DELETE -H 'Content-type: text/xml' -d @- http://127.0.0.1:8181/restconf/config/opendaylight-inventory:nodes/node/openflow:14721744650432675840/table/0/flow/2
#cat h21.xml | curl -u admin:admin -X PUT -H 'Content-type: text/xml' -d @- http://127.0.0.1:8080/restconf/config/opendaylight-inventory:nodes/node/openflow:14721744650432675840/table/1/flow/9

#curl -u admin:admin -X DELETE -H 'Content-type: text/xml' http://127.0.0.1:8181/restconf/config/opendaylight-inventory:nodes/node/openflow:14721744650432675840/table/0/flow/1
#curl -u admin:admin -X DELETE -H 'Content-type: text/xml' http://127.0.0.1:8181/restconf/config/opendaylight-inventory:nodes/node/openflow:14721744650432675840/table/0/flow/4
#curl -u admin:admin -X DELETE -H 'Content-type: text/xml' http://127.0.0.1:8181/restconf/config/opendaylight-inventory:nodes/node/openflow:2
#cat deleteflow.xml | curl -u admin:admin -X POST -H 'Content-type: text/xml' -d @- http://127.0.0.1:8181/restconf/operations/sal-flow:remove-flow
#cat addflow2.xml | curl -u admin:admin -X POST -H 'Content-type: text/xml' -d @- http://127.0.0.1:8080/restconf/operations/sal-flow:add-flow
#curl -u admin:admin -X GET http://127.0.0.1:8181/restconf/config/opendaylight-inventory:nodes/

echo ""
