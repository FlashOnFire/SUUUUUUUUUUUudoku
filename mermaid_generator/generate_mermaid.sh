#!/usr/bin/env bash

# find project root using .git folder
root=$(git rev-parse --show-toplevel)

mermaid=$(java -jar "${root}"/mermaid_generator/java2umltext-0.1.0.jar mermaid "${root}/src/main")
# clean the mermaid by remove get and set methods
mermaid=$(echo "${mermaid}" | grep -v 'get' | grep -v 'set')
md_ready="\`\`\`mermaid\n${mermaid}\n\`\`\`"

sed -i '/<!-- BEGIN_CLASS -->/,/<!-- END_CLASS -->/{//!d}' "${root}"/README.md
awk '/<!-- BEGIN_CLASS -->/ {print; print md_ready; next} 1' md_ready="${md_ready}" "${root}"/README.md > temp.md && mv temp.md "${root}"/README.md