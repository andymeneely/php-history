#!/usr/bin/ruby

require 'rexml/document'
include REXML

File.open("php-vuln-revisions.txt",'r') { | rev_file |
  rev_file.each_line{ | rev  |
    svn_log_text = `svn log -l 1 --verbose --xml http://svn.php.net/repository@#{rev}`
    xmldoc = Document.new(svn_log_text)
    XPath.each(xmldoc,"//path"){ | e | 
      filepath = e.text
      if(filepath.end_with?(".c") || filepath.end_with?(".h")) then 
		 if(filepath.start_with?("/php/php-src/")) then
		 	filepath.gsub!(/\/php\/php-src\/branches\/PHP_5_2\//,'')
			filepath.gsub!(/\/php\/php-src\/branches\/PHP_5_3\//,'')
			filepath.gsub!(/\/php\/php-src\/branches\/PHP_5_3_1\//,'')
			filepath.gsub!(/\/php\/php-src\/branches\/PHP_5_4\//,'')
			filepath.gsub!(/\/php\/php-src\/trunk\//,'')
			puts filepath
		 end
	 end 
    }
  }
}


