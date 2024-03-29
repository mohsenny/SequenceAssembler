# SequenceAssembler (version alpha)

This is the Java code for the application behind my M.Sc. thesis in Information Technology, "Sequence Assembler for Digital Storytelling".
Sequence Assembler takes some text documents (something we refer to as Fragments) and two parameters of Window Size (W) and Match Size (M) as its inputs, and the output is what we call the "Overall Story", resulted by merging those fragments.

Other libraries/JARs which must be imported to the project, but are not included in the repository are:
```
stanford-corenlp-3.3.1.jar
stanford-corenlp-3.3.1-javadoc.jar
stanford-corenlp-3.3.1-models.jar
stanford-corenlp-3.3.1-sources.jar
jaws-bin.jar (WordNet library)
```

- First 4 stanford CoreNLP libraries are available at http://stanfordnlp.github.io/CoreNLP/download.html
- The last one, WordNet dictionary is available at https://wordnet.princeton.edu/wordnet/download/

While entering the inputs keep in mind that `M` must always be smaller that `W`

If you are studying in University of Eastern Finland and are looking for more information regarding this applicaiton,
please refer to my IT project "Sequence Assembler for Digital Storytelling" written by me, Mohsen Nasiri on Autumn of 2015.

Any usage of this code with the purpose of education or futhur development is allowed. For other purposes please send an Email to mohsen.n89@gmail.com.
