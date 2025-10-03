if [ ! -d src ]; then
	echo "no src directory here";
	exit 1;
fi

if [ ! -d target ]; then
	mkdir target;
fi
cd target

javac -d . ../src/*.java
