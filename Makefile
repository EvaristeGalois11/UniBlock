thesis.pdf: thesis.tex titlePage.tex bibliografia.tex esercizi.tex
	mkdir -p bin
	pdflatex --output-directory bin/ thesis.tex
	mv ./bin/thesis.pdf ./

clean:
	rm -rf ./bin/*
