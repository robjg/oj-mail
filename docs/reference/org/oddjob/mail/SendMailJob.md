[HOME](../../../README.md)
# mail:send

Send a mail message.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [debug](#propertydebug) | Turn detailed debug messages on. | 
| [files](#propertyfiles) | A list of attachment files. | 
| [from](#propertyfrom) | The from address. | 
| [host](#propertyhost) | The host name of the server. | 
| [message](#propertymessage) | The message body as text. | 
| [name](#propertyname) | The name of the job. | 
| [password](#propertypassword) | The Password for authentication. | 
| [port](#propertyport) | The port of the server. | 
| [ssl](#propertyssl) | Use SSL for communication with the mail host. | 
| [subject](#propertysubject) | The subject. | 
| [to](#propertyto) | The to addresses. | 
| [username](#propertyusername) | The Username for authentication. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Send a simple message. |
| [Example 2](#example2) | Send attachements. |


### Property Detail
#### debug <a name="propertydebug"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Turn detailed debug messages on.

#### files <a name="propertyfiles"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

A list of attachment files.

#### from <a name="propertyfrom"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Yes.</td></tr>
</table>

The from address.

#### host <a name="propertyhost"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Yes.</td></tr>
</table>

The host name of the server.

#### message <a name="propertymessage"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>TEXT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The message body as text.

#### name <a name="propertyname"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The name of the job. Any text.

#### password <a name="propertypassword"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The Password for authentication.

#### port <a name="propertyport"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Yes.</td></tr>
</table>

The port of the server.

#### ssl <a name="propertyssl"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Use SSL for communication with the mail host.

#### subject <a name="propertysubject"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. Defaults to (no subject).</td></tr>
</table>

The subject.

#### to <a name="propertyto"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Yes.</td></tr>
</table>

The to addresses. A semicolon delimited list.

#### username <a name="propertyusername"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The Username for authentication.


### Examples
#### Example 1 <a name="example1"></a>

Send a simple message.

```xml
<oddjob xmlns:mail='http://rgordon.co.uk/oddjob/mail'>
    <job>
        <mail:send name='Send alert mail'
                   from='${my.alert.from}'
                   to='${my.alert.to}'
                   port='${my.mail.port}'
                   host='${my.mail.host}'
                   username="${my.mail.username}"
                   password="${my.mail.password}"
                   subject='Something Important'
                   debug="true"
                   ssl="${my.mail.ssl}">
            Something Important has happened!
        </mail:send>
    </job>
</oddjob>

```


#### Example 2 <a name="example2"></a>

Send attachements. This example rather unimagineatively sends the build
file as two seperate attachements.

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob id="oddjob">
    <job>
        <mail:send from="${my.alert.from}" host="${my.mail.host}" name="Send Attachments" password="${my.mail.password}" port="${my.mail.port}" ssl="true" subject="Some Attachements" to="${my.alert.to}" username="${my.mail.username}" xmlns:mail="http://rgordon.co.uk/oddjob/mail">
            <files>
                <list>
                    <values>
                        <file file="${oddjob.dir}/about.txt"/>
                        <file file="${oddjob.dir}/logo.png"/>
                    </values>
                </list>
            </files>
            <![CDATA[Check out these attachements!]]>
        </mail:send>
    </job>
</oddjob>

```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
