# Clean previously compiled class files
rm -rf *.class
rm -rf **/*.class

# Compile
javac -sourcepath . TCPChatApp.java
