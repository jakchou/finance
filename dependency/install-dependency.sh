#!/bin/bash

# 将当前目录定义为工作目录
work_dir=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

# 在工作目录中寻找所有.zip文件
for file in "$work_dir"/*.zip; do
  # 跳过没有找到的文件
  [ -e "$file" ] || continue

  file_name=$(basename "$file" .zip)

  # 解压.zip文件
  echo "解压: $file"
  mkdir -p "$work_dir/output/$file_name"
  unzip -q -n "$file" -d "$work_dir/output/$file_name"

  # 检查install文件是否存在
  if [ -e "$work_dir/output/$file_name/$file_name/install.sh" ]; then 
    cd $work_dir/output/$file_name/$file_name
    chmod +x install.sh
    ./install.sh
    cd $work_dir
  else
    # 获取pom文件名与路径的对应关系
    for jar_file in "$work_dir/output/$file_name/"*.jar; do
      jar_name=$(basename $jar_file .jar)
      mvn install:install-file -Dfile="$work_dir"/output/"$file_name"/"${jar_name}".jar -DpomFile="$work_dir"/output/"$file_name"/"${jar_name}".pom
    done
  fi
done

exit 0
